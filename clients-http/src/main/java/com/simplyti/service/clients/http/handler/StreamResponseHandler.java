package com.simplyti.service.clients.http.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;

public class StreamResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

	private final ClientChannel<Void> clientChannel;
	private final Consumer<ByteBuf> consumer;

	public StreamResponseHandler(ClientChannel<Void> clientChannel, Consumer<ByteBuf> consumer) {
		this.clientChannel=clientChannel;
		this.consumer=consumer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if(msg instanceof HttpContent && ((HttpContent) msg).content().isReadable()) {
			consumer.accept(((HttpContent) msg).content());
		}
		if(msg instanceof LastHttpContent) {
			clientChannel.promise().setSuccess(null);
			clientChannel.pipeline().remove(this);
			clientChannel.release();
		}
	}


}
