package com.simplyti.service.clients.channel;

import java.nio.channels.ClosedChannelException;

import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.proxy.Proxy;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Promise;

public class SimpleMultiplexedClientChannelFactory extends AbstractClientChannelPoolMap {
	
	private final EventLoopGroup eventLoopGroup;

	public SimpleMultiplexedClientChannelFactory(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, ChannelPoolHandler handler, SslProvider sslProvider, ClientMonitorHandler monitorHandler) {
		super(bootstrap,eventLoopGroup,handler, sslProvider,monitorHandler);
		this.eventLoopGroup=eventLoopGroup;
	}
	
	@Override
	public Future<ClientChannel> channel(Endpoint endpoint, long responseTimeoutMillis) {
		Future<ClientChannel> channel = createNewChannel(endpoint,responseTimeoutMillis);
		return channel;
	}
	
	private Future<ClientChannel> createNewChannel(Endpoint endpoint, long responseTimeoutMillis) {
		ChannelPool pool = get(endpoint);
		EventLoop loop = eventLoopGroup.next();
		Promise<ClientChannel> promise = loop.newPromise();
		io.netty.util.concurrent.Future<Channel> futureChannel = pool.acquire();
		futureChannel.addListener(f->{
			if(f.isSuccess()) {
				MultiplexedClientChannel multiplexedChannel = (MultiplexedClientChannel) futureChannel.getNow();
				if(multiplexedChannel.isActive()) {
					if(ChannelInitializedHandler.isNew(multiplexedChannel)) {
						multiplexedChannel.pipeline().addLast(new ChannelInitializedHandler(multiplexedChannel, promise));
						multiplexedChannel.pipeline().fireUserEventTriggered(ClientChannelEvent.INIT);
					} else {
						promise.setSuccess(multiplexedChannel);
					}
				} else {
					pool.release(futureChannel.getNow());
					promise.setFailure(new ClosedChannelException());
				}
			} else {
				promise.setFailure(f.cause());
			}
		});
		return new DefaultFuture<>(promise, loop);
	}

	@Override
	protected ChannelPool newPool(Bootstrap bootstrap, ChannelPoolHandler handler, Address address, Proxy proxy) {
		if(proxy!=null) {
			throw new UnsupportedOperationException();
		} else {
			return new SimpleMultiplexedChannelPool(bootstrap, handler, address);
		}
	}
	
}
