package com.simplyti.service.clients.http.sse.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.AbstractBaseHttpResponseHandler;
import com.simplyti.service.clients.http.sse.domain.ServerEvent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;

public class ServerSentEventHandler extends AbstractBaseHttpResponseHandler<ServerEvent> {

	private final Consumer<ServerEvent> consumer;

	public ServerSentEventHandler(ClientChannel channel, ByteBuf content, Promise<?> promise, Consumer<ServerEvent> consumer) {
		super(channel,content,promise);
		this.consumer=consumer;
	}

	@Override
	protected void channelRead1(ChannelHandlerContext ctx, ServerEvent msg) throws Exception {	
		consumer.accept(msg);
	}
	
}
