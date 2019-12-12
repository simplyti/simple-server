package com.simplyti.service.clients.k8s.pods;

import java.util.function.Consumer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogStreamHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private final Consumer<ByteBuf> consumer;

	public LogStreamHandler(Consumer<ByteBuf> consumer) {
		this.consumer=consumer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		consumer.accept(msg);
	}

}
