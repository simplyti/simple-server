package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Endpoint;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Promise;

public class ChannelInitializedHandler extends ChannelInboundHandlerAdapter {

	private final Promise<ClientChannel> promise;
	private final Channel channel;
	private final ChannelPool pool;
	private final Endpoint endpoint;
	private final long responseTimeoutMillis;
	private final long readTimeoutMillis;

	public ChannelInitializedHandler(Channel channel, ChannelPool pool, Endpoint endpoint, long responseTimeoutMillis, long readTimeoutMillis, Promise<ClientChannel> promise) {
		this.channel=channel;
		this.pool=pool;
		this.endpoint=endpoint;
		this.responseTimeoutMillis=responseTimeoutMillis;
		this.readTimeoutMillis=readTimeoutMillis;
		this.promise=promise;
	}
	
	 @Override
	 public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		 if(evt==ClientChannelEvent.INIT) {
			 ctx.pipeline().remove(this);
			 promise.setSuccess(new PooledClientChannel(pool,endpoint.address(), channel, responseTimeoutMillis,readTimeoutMillis));
		 } else {
			 ctx.fireUserEventTriggered(evt);
		 }
	}

}
