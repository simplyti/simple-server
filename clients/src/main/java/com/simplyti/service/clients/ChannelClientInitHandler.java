package com.simplyti.service.clients;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;

public class ChannelClientInitHandler<T> extends ChannelInboundHandlerAdapter {
	
	private static final AttributeKey<Boolean> INITIALIZED = AttributeKey.valueOf("client.initialized");

	private final Promise<ClientRequestChannel<T>> channelPromise;
	private final ClientRequestChannel<T> clientRequestChannel;

	public ChannelClientInitHandler(Promise<ClientRequestChannel<T>> channelPromise, ClientRequestChannel<T> client) {
		this.channelPromise = channelPromise;
		this.clientRequestChannel = client;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt.equals(ClientChannelEvent.INIT)) {
			ctx.pipeline().remove(this);
			channelPromise.setSuccess(clientRequestChannel);
			clientRequestChannel.attr(INITIALIZED).set(true);
		}
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.pipeline().remove(this);
		channelPromise.setFailure(cause);
    }

	public static boolean isInitialized(Channel channel) {
		return channel.attr(INITIALIZED).get()!=null;
	}
	
}
