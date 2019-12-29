package com.simplyti.service.clients.http.stream.handler;

import java.util.List;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.concurrent.Promise;

public class StreamResponseHandler extends AbstractStreamResponseHandler {

	private final Consumer<ByteBuf> consumer;

	public StreamResponseHandler(ClientChannel channel, Promise<Void> promise, List<String> originalHandlers, Consumer<ByteBuf> consumer) {
		super(channel,promise,originalHandlers,false);
		this.consumer=consumer;
	}
	
	@Override
	protected void handle(ChannelHandlerContext ctx, HttpObject msg) {
		if(msg instanceof HttpContent && ((HttpContent) msg).content().isReadable()) {
			if(consumer!=null) {
				consumer.accept(((HttpContent) msg).content());
			}else {
				ctx.fireChannelRead(((HttpContent) msg).content().retain());
			}
		}
	}

}
