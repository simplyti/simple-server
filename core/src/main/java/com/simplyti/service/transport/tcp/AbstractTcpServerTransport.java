package com.simplyti.service.transport.tcp;

import java.util.Optional;

import javax.inject.Provider;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.ssl.SslHandlerFactory;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class AbstractTcpServerTransport implements ServerTransport {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(KQueueServerTransport.class);
	
	private final ServerConfig config;
	private final Optional<SslHandlerFactory> sslHandlerFactory;
	private final Provider<EventLoopGroup> eventLoopGroup;
	private final Provider<EventLoop> startStopLoop;
	private final ChannelFactory<ServerChannel> channelFactory;
	private final ServiceChannelInitializer serviceChannelInitializer;
	
	private ChannelGroup serverChannels;

	public AbstractTcpServerTransport(Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		this.config=config;
		this.sslHandlerFactory=sslHandlerFactory;
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.channelFactory=channelFactory;
		this.serviceChannelInitializer=serviceChannelInitializer;
	}

	protected abstract void configure(ServerBootstrap bootstrap2);

	@Override
	public Future<Void> start(EventLoop executor) {
		this.serverChannels=new DefaultChannelGroup(startStopLoop.get());
		ServerBootstrap bootstrap = new ServerBootstrap().group(eventLoopGroup.get())
				.channelFactory(channelFactory)
				.option(ChannelOption.SO_BACKLOG, 8192)
				.option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
		configure(bootstrap);
		bootstrap.childHandler(serviceChannelInitializer);
		
		Promise<Void> aggregated = executor.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		config.listeners().stream()
			.filter(l->l instanceof TcpListener)
			.forEach(l->combiner.add(bind(executor, bootstrap, (TcpListener) l)));
		combiner.finish(aggregated);
		return new DefaultFuture<>(aggregated,executor);
	}
	
	private Future<Void> bind(EventLoop executor, ServerBootstrap bootstrap, TcpListener listener) {
		if(listener.ssl() && !sslHandlerFactory.isPresent()) {
			return new DefaultFuture<>(executor.newSucceededFuture(null),executor);
		}
		
		Promise<Void> futureBind = executor.newPromise();
		log.info("Starting service listener on port {}", listener.port());
		ChannelFuture channelFuture = bootstrap.bind(listener.port());
		channelFuture.addListener((ChannelFuture future) -> {
			if (future.isSuccess()) {
				log.info("Listening on {}", future.channel().localAddress());
				channelFuture.channel().attr(ServerTransport.LISTENER).set(listener);
				this.serverChannels.add(channelFuture.channel());
				futureBind.setSuccess(null);
			} else {
				log.warn("Error listening on port {}: {}", listener.port(), future.cause().getMessage());
				futureBind.setFailure(future.cause());
			}
		});
		return new DefaultFuture<>(futureBind,executor);
	}
	
	@Override
	public Future<Void> stop(EventLoop eventLoop) {
		if(serverChannels == null) {
			return new DefaultFuture<>(eventLoop.newSucceededFuture(null),eventLoop);
		}
		Promise<Void> promise = eventLoop.newPromise();
		Promises.toPromise(serverChannels.close(), promise);
		return new DefaultFuture<>(promise, eventLoop);
	}

}
