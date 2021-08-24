package com.simplyti.service.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class ClientResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

	private final Promise<SimpleHttpResponse> promise;
	private final ChannelPool pool;

	public ClientResponseHandler(Promise<SimpleHttpResponse> promise, ChannelPool pool) {
		this.promise=promise;
		this.pool=pool;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
		ctx.channel().pipeline().remove(this);
		pool.release(ctx.channel());
		promise.setSuccess(new SimpleHttpResponse(msg.content().toString(CharsetUtil.UTF_8), msg.status(),msg.headers()));
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		pool.release(ctx.channel());
		promise.tryFailure(new ClosedChannelException());
    }
	
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(!ctx.channel().isActive()) {
			channelInactive(ctx);
		}
	}

}
