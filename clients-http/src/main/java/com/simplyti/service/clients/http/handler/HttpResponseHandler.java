package com.simplyti.service.clients.http.handler;

import java.nio.channels.ClosedChannelException;
import java.util.function.Consumer;

import com.simplyti.service.clients.ClientRequestChannel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

	private final ClientRequestChannel<Void> clientChannel;
	private final Consumer<HttpObject> consumer;

	public HttpResponseHandler(ClientRequestChannel<Void> clientChannel, Consumer<HttpObject> consumer) {
		this.clientChannel=clientChannel;
		this.consumer=consumer;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientChannel.setFailure(new ClosedChannelException());
		clientChannel.release();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		consumer.accept(msg);
		if(msg instanceof LastHttpContent) {
			clientChannel.setSuccess(null);
			clientChannel.pipeline().remove(this);
			clientChannel.release();
		}
	}
	
}
