package com.simplyti.service;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.matcher.di.InstanceProvider;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.util.concurrent.DefaultFuture;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import sun.misc.Signal;

@SuppressWarnings("restriction")
public class DefaultServer implements Server {
	
	private final Thread jvmShutdownHook = new Thread(() -> {
		try {
			this.stop(true, true).await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}, "server-shutdown-hook");
	private static final InternalLogger log = InternalLoggerFactory.getInstance(DefaultServer.class);
	
	private final EventLoopGroup eventLoopGroup;
	private final EventLoop startStopLoop;
	private final ClientChannelGroup clientChannelGroup;
	private final Set<ServerStartHook> serverStartHook;
	private final Set<ServerStopHook> serverStopHook;
	private final ServerConfig config;
	private final ServerTransport transport;
	
	private final ServerStopAdvisor startStopMonitor;
	
	private final InstanceProvider instanceProvider;
	
	private Promise<Server> startPromise;
	private Promise<Void> stopPromise;
	
	@Inject
	public DefaultServer(EventLoopGroup eventLoopGroup, ServerStopAdvisor startStopMonitor,
			@StartStopLoop EventLoop startStopLoop, ClientChannelGroup clientChannelGroup,
			Set<ServerStartHook> serverStartHook,
			Set<ServerStopHook> serverStopHook,
			ServerConfig config, ServerTransport transport,
			InstanceProvider instanceProvider){
		Signal.handle(new Signal("INT"), sig -> System.exit(0));
		Signal.handle(new Signal("TERM"), sig -> System.exit(0));
		Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.startStopMonitor=startStopMonitor;
		this.clientChannelGroup=clientChannelGroup;
		this.serverStartHook=serverStartHook;
		this.serverStopHook=serverStopHook;
		this.config=config;
		this.transport=transport;
		this.instanceProvider=instanceProvider;
	}
	
	public Future<Server> start() {
		Promise<Server> startPromise = startStopLoop.newPromise();
		if(startStopLoop.inEventLoop()) {
			return start(startStopLoop,startPromise);
		}else {
			startStopLoop.execute(()->start(startStopLoop,startPromise));
		}
		return startPromise;
	}
	
	private Future<Server> start(EventLoop executor, Promise<Server> startPromise) {
		if(this.startPromise != null) {
			Promises.toPromise(this.startPromise, startPromise);
			return startPromise;
		}
		this.startPromise = startPromise;
		if(serverStartHook.isEmpty()) {
			start0(executor,startPromise);
		}else {
			executeStartHooks(executor).addListener(f->{
				if(f.isSuccess()) {
					start0(executor,startPromise);
				}else {
					stop().addListener(ignore -> startPromise.setFailure(f.cause()));
				}
			});
		}
		return startPromise;
	}

	private Future<Void> executeStartHooks(EventLoop executor) {
		Promise<Void> hooksPromise = startStopLoop.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		this.serverStartHook.forEach(hook->combiner.add(hook.executeStart(startStopLoop)));
		combiner.finish(hooksPromise);
		return hooksPromise;
	}

	private void start0(EventLoop executor,Promise<Server> startPromise) {
		transport.start(startStopLoop)
			.thenAccept(v->startPromise.setSuccess(this))
			.exceptionally(err->stop().addListener(ignore -> startPromise.setFailure(err)));
	}

	public Future<Void> stop() {
		return stop(false, false);
	}
	
	public Future<Void> stop(boolean loopsWait) {
		return stop(loopsWait, false);
	}
	
	public Future<Void> stop(boolean loopsWait, boolean fromHook) {
		Promise<Void> stopPromise = startStopLoop.newPromise();
		if(startStopLoop.inEventLoop()){
			return stop0(startStopLoop, stopPromise, loopsWait, fromHook);
		}else{
			startStopLoop.execute(()->stop0(startStopLoop, stopPromise, loopsWait, fromHook));
		}
		return stopPromise;
	}
	
	private Future<Void> stop0(EventLoop executor, Promise<Void> stopPromise, boolean loopsWait, boolean fromHook) {
		if(this.stopPromise != null) {
			Promises.toPromise(this.stopPromise, stopPromise);
			return stopPromise;
		}
		this.stopPromise = stopPromise;
		
		if(!fromHook) {
			Runtime.getRuntime().removeShutdownHook(jvmShutdownHook);
		}
		this.startStopMonitor.stopAdvice();
		log.info("Stopping server gracefully...");
		transport.stop(executor)
			.handleCombine((v,err)->closeClients(executor))
			.handleCombine((v,err)->executeStopHooks(executor))
			.handle((v,err)->stopEventLoops(loopsWait, stopPromise, executor));
		return stopPromise;
	}
	
	private void stopEventLoops(boolean loopsWait, Promise<Void> stopPromise, EventLoop executor) {
		if(config.externalEventLoopGroup()) {
			startStopLoop.shutdownGracefully();
			stopPromise.setSuccess(null);
		} else if(loopsWait){
			eventLoopGroup.shutdownGracefully().addListener(f->{
				startStopLoop.shutdownGracefully();
				stopPromise.setSuccess(null);
			});
		} else {
			eventLoopGroup.shutdownGracefully();
			startStopLoop.shutdownGracefully();
			stopPromise.setSuccess(null);
		}
	}
	
	private com.simplyti.util.concurrent.Future<Void> closeClients(EventLoop executor) {
		if(clientChannelGroup.isEmpty()) {
			return new DefaultFuture<>(executor.newSucceededFuture(null),executor);
		}else {
			log.info("Active Clients: {}",clientChannelGroup.size());
			ChannelGroupFuture future = clientChannelGroup.newCloseFuture();
			clientChannelGroup.closeIddleChannels();
			Promise<Void> promise = executor.newPromise();
			future.addListener(f->promise.setSuccess(null));
			return new DefaultFuture<>(promise,executor);
		}
	}
	
	private Future<Void> executeStopHooks(EventLoop executor) {
		log.info("Executing stop hooks...");
		if(this.serverStopHook.isEmpty()) {
			return executor.newSucceededFuture(null);
		}
		Promise<Void> promise = executor.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		this.serverStopHook.forEach(hook->combiner.add(hook.executeStop(startStopLoop)));
		combiner.finish(promise);
		return promise;
	}
	
	@Override
	public Future<Void> stopFuture() {
		return startStopLoop.newPromise();
	}
	
	@Override
	public <O> O instance(Class<O> clazz) {
		return instanceProvider.get(clazz);
	}
	
	@Override
	public <O> O instanceAnnotatedWith(Class<O> clazz, Class<? extends Annotation> ann) {
		return instanceProvider.get(clazz,ann);
	}

}
