package com.simplyti.service;

import static io.vavr.control.Try.of;
import static io.vavr.control.Try.run;

import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DefaultService implements Service{
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ServerBootstrap bootstrap;
	private final EventLoopGroup eventLoopGroup;
	private final EventLoop startStopLoop;
	private final Promise<Void> stopFuture;
	private final ServerConfig config;
	private final ClientChannelGroup clientChannelGroup;
	private final Set<ServerStartHook> serverStartHook;
	private final Set<ServerStopHook> serverStopHook;
	
	private final ChannelGroup serverChannels;
	
	private boolean stopping;
	
	private final Thread jvmShutdownHook = new Thread(() -> of(this.stop()::await), "server-shutdown-hook");

	@Inject
	public DefaultService(EventLoopGroup eventLoopGroup,ServiceChannelInitializer serviceChannelInitializer,
			@StartStopLoop EventLoop startStopLoop, ServerConfig config, ClientChannelGroup clientChannelGroup,
			Class<? extends ServerSocketChannel> serverChannelClass, Set<ServerStartHook> serverStartHook,
			Set<ServerStopHook> serverStopHook){
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.stopFuture=startStopLoop.newPromise();
		this.config=config;
		this.clientChannelGroup=clientChannelGroup;
		this.serverStartHook=serverStartHook;
		this.serverStopHook=serverStopHook;
		this.serverChannels=new DefaultChannelGroup(startStopLoop);
		
		this.bootstrap = new ServerBootstrap().group(startStopLoop, eventLoopGroup).channel(serverChannelClass)
				.option(ChannelOption.SO_BACKLOG, 100000)
				.option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
				.childHandler(serviceChannelInitializer);
	}
	
	public Future<Service> start() {
		Promise<Service> startPromise = executor().newPromise();
		if(serverStartHook.isEmpty()) {
			start(startPromise);
		}else {
			executeStartHooks().addListener(f->{
				if(f.isSuccess()) {
					start(startPromise);
				}else {
					stop().addListener(stopFuture -> startPromise.setFailure(f.cause()));
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

	private void start(Promise<Service> startPromise) {
		PromiseCombiner combiner = new PromiseCombiner();
		Promise<Void> bindPromise = executor().newPromise();
		combiner.add(bind(config.insecuredPort()));
		combiner.add(bind(config.securedPort()));
		combiner.finish(bindPromise);
		bindPromise.addListener(f->{
			if(f.isSuccess()) {
				startPromise.setSuccess(this);
			}else {
				stop().addListener(stopFuture -> startPromise.setFailure(bindPromise.cause()));
			}
		});
		
	}

	private Future<Void> bind(int port) {
		Promise<Void> futureBind = executor().newPromise();
		log.info("Starting service listener on port {}", port);
		ChannelFuture channelFuture = bootstrap.bind(port);
		channelFuture.addListener((ChannelFuture future) -> {
			if (future.isSuccess()) {
				log.info("Listening on {}", future.channel().localAddress());
				this.serverChannels.add(channelFuture.channel());
				futureBind.setSuccess(null);
			} else {
				log.warn("Error listening on port {}: {}", port, future.cause().getMessage());
				futureBind.setFailure(future.cause());
			}
		});
		return futureBind;
	}

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
		serverChannels.close().addListener(f->closeClients(clientsFuture));
		Promise<Void> hooksFuture = executor().newPromise();
		clientsFuture.addListener(f->executeStopHooks(hooksFuture));
		hooksFuture.addListener(f->stopLoops());
		return stopFuture;
	}
	
	private void stopLoops() {
		log.info("Stopping executors...");
		run(()->Runtime.getRuntime().removeShutdownHook(jvmShutdownHook));
		stopFuture.setSuccess(null);
		eventLoopGroup.shutdownGracefully();
		executor().shutdownGracefully();
	}

	private void executeStopHooks(Promise<Void> hooksFuture) {
		log.info("Executing stop hooks...");
		PromiseCombiner combiner = new PromiseCombiner();
		this.serverStopHook.forEach(hook->combiner.add(hook.executeStop(executor())));
		combiner.finish(hooksFuture);
	}

	private void closeClients(Promise<Void> clientsFuture) {
		log.info("Active Clients: {}",clientChannelGroup.size());
		clientChannelGroup.closeIddleChannels();
		clientChannelGroup.newCloseFuture().addListener(f->clientsFuture.setSuccess(null));
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

}
