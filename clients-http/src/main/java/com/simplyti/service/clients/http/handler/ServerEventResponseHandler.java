package com.simplyti.service.clients.http.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.http.sse.ServerEvent;
import com.simplyti.service.clients.http.sse.ServerSentEventDecoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpResponse;

public class ServerEventResponseHandler extends SimpleChannelInboundHandler<Object> {

	private final Consumer<ServerEvent> consumer;
	private final ClientRequestChannel<Void> clientChannel;

	public ServerEventResponseHandler(ClientRequestChannel<Void> channel, Consumer<ServerEvent> consumer) {
		this.clientChannel=channel;
		this.consumer=consumer;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(!clientChannel.isDone()) {
			clientChannel.setSuccess(null);
		}
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpResponse) {
			ctx.pipeline().replace(HttpClientCodec.class, "sse-decoder", new ServerSentEventDecoder());
			ctx.pipeline().addBefore("sse-decoder", "line-decoder", new LineBasedFrameDecoder(100000));
		}else {
			consumer.accept((ServerEvent) msg);
		}
	}


}
