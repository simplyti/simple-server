package com.simplyti.service;

import static io.vavr.control.Try.of;
import static io.vavr.control.Try.run;

import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class AbstractService<T extends Service<T>> implements Service<T> {
	
	private final Thread jvmShutdownHook = new Thread(() -> of(this.stop()::await), "server-shutdown-hook");
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final EventLoopGroup eventLoopGroup;
	private final EventLoop startStopLoop;
	private final Promise<Void> stopFuture;
	private final ClientChannelGroup clientChannelGroup;
	private final Set<ServerStartHook> serverStartHook;
	private final Set<ServerStopHook> serverStopHook;
	private final ServerConfig config;
	
	private boolean stopping;
	
	
	@Inject
	public AbstractService(EventLoopGroup eventLoopGroup,
			@StartStopLoop EventLoop startStopLoop, ClientChannelGroup clientChannelGroup,
			Set<ServerStartHook> serverStartHook,
			Set<ServerStopHook> serverStopHook,
			ServerConfig config){
		Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.stopFuture=startStopLoop.newPromise();
		this.clientChannelGroup=clientChannelGroup;
		this.serverStartHook=serverStartHook;
		this.serverStopHook=serverStopHook;
		this.config=config;
	}
	
	public Future<T> start() {
		Promise<T> startPromise = executor().newPromise();
		if(serverStartHook.isEmpty()) {
			start(startPromise);
		}else {
			executeStartHooks().addListener(f->{
				if(f.isSuccess()) {
					start(startPromise);
				}else {
					stop().addListener(ignore -> startPromise.setFailure(f.cause()));
				}
			});
		}
		return startPromise;
	}
	
	private Future<Void> executeStartHooks() {
		Promise<Void> hooksPromise = executor().newPromise();
		PromiseCombiner combiner = new PromiseCombiner();
		this.serverStartHook.forEach(hook->combiner.add(hook.executeStart(executor())));
		combiner.finish(hooksPromise);
		return hooksPromise;
	}

	@SuppressWarnings("unchecked")
	private void start(Promise<T> startPromise) {
		PromiseCombiner combiner = new PromiseCombiner();
		Promise<Void> bindPromise = executor().newPromise();
		combiner.add(bind(executor()));
		combiner.finish(bindPromise);
		bindPromise.addListener(f->{
			if(f.isSuccess()) {
				startPromise.setSuccess((T) this);
			}else {
				stop().addListener(ignore -> startPromise.setFailure(bindPromise.cause()));
			}
		});
	}

	protected abstract Future<Void> bind(EventLoop executor);
	
	public Future<Void> stop() {
		if(executor().inEventLoop()){
			return stop0();
		}else{
			executor().submit(this::stop0);
		}
		return stopFuture;
	}
	
	private Future<Void> stop0() {
		if(this.stopping){
			return stopFuture;
		}
		this.stopping=true;
		log.info("Stopping server gracefully...");
		Promise<Void> clientsFuture = executor().newPromise();
		undbind(executor()).addListener(f->closeClients(clientsFuture));
		Promise<Void> hooksFuture = executor().newPromise();
		clientsFuture.addListener(f->executeStopHooks(hooksFuture));
		hooksFuture.addListener(f->stopLoops());
		return stopFuture;
	}
	
	protected abstract Future<Void> undbind(EventLoop executor);

	private void closeClients(Promise<Void> clientsFuture) {
		log.info("Active Clients: {}",clientChannelGroup.size());
		clientChannelGroup.closeIddleChannels();
		clientChannelGroup.newCloseFuture().addListener(f->clientsFuture.setSuccess(null));
	}
	
	private void executeStopHooks(Promise<Void> hooksFuture) {
		log.info("Executing stop hooks...");
		PromiseCombiner combiner = new PromiseCombiner();
		this.serverStopHook.forEach(hook->combiner.add(hook.executeStop(executor())));
		combiner.finish(hooksFuture);
	}
	
	private void stopLoops() {
		log.info("Stopping executors...");
		run(()->Runtime.getRuntime().removeShutdownHook(jvmShutdownHook));
		stopFuture.setSuccess(null);
		if(!config.externalEventLoopGroup()) {
			eventLoopGroup.shutdownGracefully();
		}
		
		executor().shutdownGracefully();
	}
	
	public Future<Void> stopFuture() {
		return stopFuture;
	}

	@Override
	public boolean stopping() {
		return stopping;
	}
	
	@Override
	public EventLoop executor() {
		return startStopLoop;
	}
	
	public EventLoopGroup eventLoopGroup() {
		return eventLoopGroup;
	}

}
