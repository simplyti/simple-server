package com.simplyti.service;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

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
	
	private final Provider<EventLoopGroup> eventLoopGroup;
	private final Provider<EventLoop> startStopLoop;
	private final Provider<ClientChannelGroup> clientChannelGroup;
	private final Set<ServerStartHook> serverStartHook;
	private final Set<ServerStopHook> serverStopHook;
	private final ServerConfig config;
	private final Set<ServerTransport> transport;
	
	private final ServerStopAdvisor startStopMonitor;
	
	private final InstanceProvider instanceProvider;
	
	private Promise<Server> startPromise;
	private Promise<Void> stopPromise;
	
	@Inject
	public DefaultServer(Provider<EventLoopGroup> eventLoopGroup, ServerStopAdvisor startStopMonitor,
			@StartStopLoop Provider<EventLoop> startStopLoop, Provider<ClientChannelGroup> clientChannelGroup,
			Set<ServerStartHook> serverStartHook,
			Set<ServerStopHook> serverStopHook,
			ServerConfig config, Set<ServerTransport> transport,
			InstanceProvider instanceProvider){
		Signal.handle(new Signal("INT"), sig -> System.exit(0));
		Signal.handle(new Signal("TERM"), sig -> System.exit(0));
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
		Promise<Server> startPromise = startStopLoop.get().newPromise();
		if(startStopLoop.get().inEventLoop()) {
			return start(startStopLoop.get(),startPromise);
		}else {
			startStopLoop.get().execute(()->start(startStopLoop.get(),startPromise));
		}
		return startPromise;
	}
	
	private Future<Server> start(EventLoop executor, Promise<Server> startPromise) {
		if(this.startPromise != null) {
			Promises.toPromise(this.startPromise, startPromise);
			return startPromise;
		}
		this.startPromise = startPromise;
		Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
		if(serverStartHook.isEmpty()) {
			start0(executor,startPromise);
		} else {
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
		Promise<Void> hooksPromise = startStopLoop.get().newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		this.serverStartHook.forEach(hook->combiner.add(hook.executeStart(startStopLoop.get())));
		combiner.finish(hooksPromise);
		return hooksPromise;
	}

	private void start0(EventLoop executor,Promise<Server> startPromise) {
		startTransport(executor)
			.thenAccept(v->startPromise.setSuccess(this))
			.exceptionally(err->stop().addListener(ignore -> startPromise.setFailure(err)));
	}

	private com.simplyti.util.concurrent.Future<Void> startTransport(EventLoop executor) {
		PromiseCombiner combiner = new PromiseCombiner(executor);
		transport.forEach(t->{
			combiner.add(t.start(startStopLoop.get()));
		});
		Promise<Void> end = executor.newPromise();
		combiner.finish(end);
		return new DefaultFuture<>(end, executor);
	}

	public Future<Void> stop() {
		return stop(false, false);
	}
	
	public Future<Void> stop(boolean loopsWait) {
		return stop(loopsWait, false);
	}
	
	public Future<Void> stop(boolean loopsWait, boolean fromHook) {
		Promise<Void> stopPromise = startStopLoop.get().newPromise();
		if(startStopLoop.get().inEventLoop()){
			return stop0(startStopLoop.get(), stopPromise, loopsWait, fromHook);
		}else{
			startStopLoop.get().execute(()->stop0(startStopLoop.get(), stopPromise, loopsWait, fromHook));
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
		stopTransport(executor)
			.handleCombine((v,err)->closeClients(executor))
			.handleCombine((v,err)->executeStopHooks(executor))
			.handle((v,err)->stopEventLoops(loopsWait, stopPromise, executor));
		return stopPromise;
	}
	
	private com.simplyti.util.concurrent.Future<Void> stopTransport(EventLoop executor) {
		PromiseCombiner combiner = new PromiseCombiner(executor);
		transport.forEach(t->{
			combiner.add(t.stop(startStopLoop.get()));
		});
		Promise<Void> end = executor.newPromise();
		combiner.finish(end);
		return new DefaultFuture<>(end, executor);
	}
	
	private void stopEventLoops(boolean loopsWait, Promise<Void> stopPromise, EventLoop executor) {
		if(config.externalEventLoopGroup()) {
			startStopLoop.get().shutdownGracefully();
			stopPromise.setSuccess(null);
		} else if(loopsWait){
			eventLoopGroup.get().shutdownGracefully().addListener(f->{
				startStopLoop.get().shutdownGracefully();
				stopPromise.setSuccess(null);
			});
		} else {
			eventLoopGroup.get().shutdownGracefully();
			startStopLoop.get().shutdownGracefully();
			stopPromise.setSuccess(null);
		}
	}
	
	private com.simplyti.util.concurrent.Future<Void> closeClients(EventLoop executor) {
		if(clientChannelGroup.get().isEmpty()) {
			return new DefaultFuture<>(executor.newSucceededFuture(null),executor);
		}else {
			log.info("Active Clients: {}",clientChannelGroup.get().size());
			ChannelGroupFuture future = clientChannelGroup.get().newCloseFuture();
			clientChannelGroup.get().closeIddleChannels();
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
		this.serverStopHook.forEach(hook->combiner.add(hook.executeStop(startStopLoop.get())));
		combiner.finish(promise);
		return promise;
	}
	
	@Override
	public Future<Void> stopFuture() {
		return startStopLoop.get().newPromise();
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
