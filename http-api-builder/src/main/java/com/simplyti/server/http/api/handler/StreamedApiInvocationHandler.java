package com.simplyti.server.http.api.handler;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Promise;

public class StreamedApiInvocationHandler extends SimpleChannelInboundHandler<HttpContent>{

	private final Consumer<ByteBuf> consumer;
	private final Promise<Void> promise;
	

	public StreamedApiInvocationHandler(Consumer<ByteBuf> consumer, Promise<Void> promise) {
		this.consumer=consumer;
		this.promise=promise;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
		consumer.accept(((HttpContent) msg).content());
		if(msg instanceof LastHttpContent) {
			ctx.pipeline().remove(this);
			promise.setSuccess(null);
		}
	}

}
