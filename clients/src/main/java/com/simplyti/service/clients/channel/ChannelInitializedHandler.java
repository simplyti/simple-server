package com.simplyti.service.clients.channel;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;

public class ChannelInitializedHandler extends ChannelInboundHandlerAdapter {
	
	private static final AttributeKey<Boolean> INITIALIZED = AttributeKey.valueOf("clients.init");

	private final Promise<ClientChannel> promise;
	private final PooledClientChannel pooledClient;

	public ChannelInitializedHandler(PooledClientChannel pooledClient, Promise<ClientChannel> promise) {
		this.pooledClient=pooledClient;
		this.promise=promise;
	}
	
	 @Override
	 public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		 if(evt==ClientChannelEvent.INIT) {
			 pooledClient.attr(INITIALIZED).set(true);
			 ctx.pipeline().remove(this);
			 promise.setSuccess(pooledClient);
		 } else {
			 ctx.fireUserEventTriggered(evt);
		 }
	}
	 
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		pooledClient.release();
		promise.setFailure(cause);
	}

	public static boolean isNew(Channel channel) {
		return channel.attr(INITIALIZED).get() != Boolean.TRUE;
	}

}
