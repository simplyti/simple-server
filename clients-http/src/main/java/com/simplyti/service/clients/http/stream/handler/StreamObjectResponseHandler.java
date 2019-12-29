package com.simplyti.service.clients.http.stream.handler;

import java.util.List;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Promise;

public class StreamObjectResponseHandler extends AbstractStreamResponseHandler {

	private final Consumer<HttpObject> consumer;

	public StreamObjectResponseHandler(ClientChannel channel, Promise<Void> promise, List<String> originalHandlers, Consumer<HttpObject> consumer) {
		super(channel,promise,originalHandlers,true);
		this.consumer=consumer;
	}

	@Override
	protected void handle(ChannelHandlerContext ctx, HttpObject msg) {
		consumer.accept(msg);
	}
	
}
