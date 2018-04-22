package com.simplyti.service.clients.http.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientChannel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

	private final ClientChannel<Void> clientChannel;
	private final Consumer<HttpObject> consumer;

	public HttpResponseHandler(ClientChannel<Void> clientChannel, Consumer<HttpObject> consumer) {
		this.clientChannel=clientChannel;
		this.consumer=consumer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		consumer.accept(msg);
		if(msg instanceof LastHttpContent) {
			clientChannel.promise().setSuccess(null);
			clientChannel.pipeline().remove(this);
			clientChannel.release();
		}
	}
	
}
