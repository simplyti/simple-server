package com.simplyti.service.transport.unix;

import javax.inject.Provider;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.service.config.ServerConfig;
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
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class UnixDomainTransport implements ServerTransport {

private static final InternalLogger log = InternalLoggerFactory.getInstance(UnixDomainTransport.class);

	private final ServerConfig config;
	private final Provider<EventLoopGroup> eventLoopGroup;
	private final Provider<EventLoop> startStopLoop;
	private final ChannelFactory<ServerDomainSocketChannel> channelFactory;
	private final ServiceChannelInitializer serviceChannelInitializer;
	
	private ChannelGroup serverChannels;

	public UnixDomainTransport(Provider<EventLoopGroup> eventLoopGroup, @StartStopLoop Provider<EventLoop> startStopLoop,
			ChannelFactory<ServerDomainSocketChannel> channelFactory, 
			ServiceChannelInitializer serviceChannelInitializer, ServerConfig config) {
		this.config=config;
		this.eventLoopGroup=eventLoopGroup;
		this.startStopLoop=startStopLoop;
		this.channelFactory=channelFactory;
		this.serviceChannelInitializer=serviceChannelInitializer;
	}

	protected  void configure(ServerBootstrap bootstrap2) {
		
	}

	@Override
	public Future<Void> start(EventLoop executor) {
		this.serverChannels=new DefaultChannelGroup(startStopLoop.get());
		ServerBootstrap bootstrap = new ServerBootstrap().group(eventLoopGroup.get())
				.channelFactory(channelFactory)
				.option(ChannelOption.SO_BACKLOG, 8192)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
		configure(bootstrap);
		bootstrap.childHandler(serviceChannelInitializer);
		
		Promise<Void> aggregated = executor.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		config.listeners().stream()
			.filter(l->l instanceof UnixDomainListener)
			.forEach(l->combiner.add(bind(executor,bootstrap, (UnixDomainListener) l)));
		combiner.finish(aggregated);
		return new DefaultFuture<>(aggregated,executor);
	}
	
	private Future<Void> bind(EventLoop executor, ServerBootstrap bootstrap, UnixDomainListener listener) {
		DomainSocketAddress socketAddress = new DomainSocketAddress(listener.file());
		Promise<Void> futureBind = executor.newPromise();
		log.info("Starting service listener on port {}", socketAddress);
		ChannelFuture channelFuture = bootstrap.bind(socketAddress);
		channelFuture.addListener((ChannelFuture future) -> {
			if (future.isSuccess()) {
				log.info("Listening on {}", future.channel().localAddress());
				future.channel().attr(ServerTransport.LISTENER).set(listener);
				this.serverChannels.add(channelFuture.channel());
				futureBind.setSuccess(null);
			} else {
				log.warn("Error listening on port {}: {}", socketAddress, future.cause().getMessage());
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
