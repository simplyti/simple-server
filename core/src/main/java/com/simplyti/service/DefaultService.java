package com.simplyti.service;

import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.builder.di.StartStopLoop;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.ServiceChannelInitializer;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.ssl.SslHandlerFactory;

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
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DefaultService extends AbstractService<DefaultService> implements Service<DefaultService>{
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(DefaultService.class);

	public static final AttributeKey<Listener> LISTENER_ATT = AttributeKey.valueOf("server.listener");
	
	private final ServerBootstrap bootstrap;
	private final ServerConfig config;
	private final ChannelGroup serverChannels;
	private final Optional<SslHandlerFactory> sslHandlerFactory;
	
	@Inject
	public DefaultService(EventLoopGroup eventLoopGroup, StartStopMonitor startStopMonitor, ServiceChannelInitializer serviceChannelInitializer,
			@StartStopLoop EventLoop startStopLoop, ServerConfig config, ClientChannelGroup clientChannelGroup,
			ChannelFactory<ServerChannel> channelFactory, Optional<SslHandlerFactory> sslHandlerFactory,
			Set<ServerStartHook> serverStartHook, Set<ServerStopHook> serverStopHook){
		super(eventLoopGroup,startStopMonitor,startStopLoop,clientChannelGroup,serverStartHook,serverStopHook,config);
		this.config=config;
		this.serverChannels=new DefaultChannelGroup(startStopLoop);
		this.sslHandlerFactory=sslHandlerFactory;
		this.bootstrap = new ServerBootstrap().group(startStopLoop, eventLoopGroup)
				.channelFactory(channelFactory)
				.option(ChannelOption.SO_BACKLOG, 100000)
				.option(ChannelOption.SO_REUSEADDR, true)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
				.childHandler(serviceChannelInitializer);
	}
	
	protected Future<Void> bind(EventLoop executor){
		Promise<Void> aggregated = executor.newPromise();
		PromiseCombiner combiner = new PromiseCombiner(executor);
		config.listeners().forEach(listener->combiner.add(bind(executor,listener)));
		combiner.finish(aggregated);
		return aggregated;
	}
	
	private Future<Void> bind(EventLoop executor, Listener listener) {
		if(listener.ssl() &&  !sslHandlerFactory.isPresent()) {
			return executor.newSucceededFuture(null);
		}
		Promise<Void> futureBind = executor.newPromise();
		log.info("Starting service listener on port {}", listener.port());
		ChannelFuture channelFuture = bootstrap.bind(listener.port());
		channelFuture.addListener((ChannelFuture future) -> {
			if (future.isSuccess()) {
				log.info("Listening on {}", future.channel().localAddress());
				channelFuture.channel().attr(LISTENER_ATT).set(listener);
				this.serverChannels.add(channelFuture.channel());
				futureBind.setSuccess(null);
			} else {
				log.warn("Error listening on port {}: {}", listener.port(), future.cause().getMessage());
				futureBind.setFailure(future.cause());
			}
		});
		return futureBind;
	}

	protected Future<Void> undbind(EventLoop executor){
		return serverChannels.close();
	}

}
