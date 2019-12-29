package com.simplyti.service.clients.http.sse.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.http.sse.ServerEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.concurrent.Promise;

public class ServerEventResponseHandler extends SimpleChannelInboundHandler<Object> {

	private final Promise<Void> promise;
	private final Consumer<ServerEvent> consumer;
	private final String handlerName;

	public ServerEventResponseHandler(String handlerName, Promise<Void> promise, Consumer<ServerEvent> consumer) {
		this.handlerName=handlerName;
		this.promise=promise;
		this.consumer=consumer;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(!promise.isDone()) {
			promise.setSuccess(null);
		}
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpResponse) {
			ctx.pipeline().addBefore(handlerName, "sse-decoder", new ServerSentEventDecoder());
		}else {
			consumer.accept((ServerEvent) msg);
		}
	}


}