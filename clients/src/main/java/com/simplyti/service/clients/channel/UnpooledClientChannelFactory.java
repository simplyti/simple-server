package com.simplyti.service.clients.channel;

import java.net.InetSocketAddress;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.monitor.MonitoredHandler;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Promise;

public class UnpooledClientChannelFactory extends ChannelInitializer<SocketChannel> implements ClientChannelFactory {
	
	private final Bootstrap bootstrap;
	private final EventLoopGroup eventLoopGroup;
	private final ChannelPoolHandler handler;
	
	public UnpooledClientChannelFactory(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, ChannelPoolHandler handler, SslProvider sslProvider, ClientMonitorHandler monitor) {
		this.bootstrap=bootstrap;
		this.eventLoopGroup=eventLoopGroup;
		this.handler=monitor!=null?new MonitoredHandler(monitor,handler):handler;
	}
	
	public Future<ClientChannel> channel(Endpoint endpoint, long responseTimeoutMillis, long readTimeoutMillis) {
		EventLoop loop = eventLoopGroup.next();
		ChannelFuture channelFuture = bootstrap.clone().handler(this)
				.connect(new InetSocketAddress(endpoint.address().host(), endpoint.address().port()));
		if(channelFuture.isDone()) {
			if(channelFuture.isSuccess()) {
				Channel channel = channelFuture.channel();
				return new DefaultFuture<>(loop.newSucceededFuture(new UnpooledClientChannel(channel,endpoint.address())), loop);
			} else {
				return new DefaultFuture<>(loop.newFailedFuture(channelFuture.cause()), loop);
			}
		} else {
			Promise<ClientChannel> promise = loop.newPromise();
			channelFuture.addListener(f -> {
				if (channelFuture.isSuccess()) {
					Channel channel = channelFuture.channel();
					promise.setSuccess(new UnpooledClientChannel(channel,endpoint.address()));
				} else {
					promise.setFailure(channelFuture.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {	
		handler.channelAcquired(ch);
		handler.channelCreated(ch);
	}

}
