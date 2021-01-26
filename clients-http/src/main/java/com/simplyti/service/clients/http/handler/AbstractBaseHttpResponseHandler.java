package com.simplyti.service.clients.http.handler;

import java.nio.channels.ClosedChannelException;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

public abstract class AbstractBaseHttpResponseHandler<T> extends SimpleChannelInboundHandler<T> {
	
	private final ClientChannel channel;
	private final Promise<?> promise;

	protected AbstractBaseHttpResponseHandler(ClientChannel channel, Promise<?> promise) {
		super(false);
		this.channel=channel;
		this.promise=promise;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		promise.tryFailure(new ClosedChannelException());
		channel.release();
    }
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(!ctx.channel().isActive()) {
			channelInactive(ctx);
		}
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		promise.tryFailure(cause);
		channel.close().addListener(f->channel.release());
    }
	
}
