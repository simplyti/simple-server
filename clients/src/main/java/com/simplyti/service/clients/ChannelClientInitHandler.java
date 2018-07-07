package com.simplyti.service.clients;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;

public class ChannelClientInitHandler<T> extends ChannelInboundHandlerAdapter {

	private final Promise<ClientRequestChannel<T>> channelPromise;
	private final ClientRequestChannel<T> client;

	public ChannelClientInitHandler(Promise<ClientRequestChannel<T>> channelPromise, ClientRequestChannel<T> client) {
		this.channelPromise = channelPromise;
		this.client = client;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt.equals(ClientChannelEvent.INIT) && channelPromise!=null) {
			channelPromise.setSuccess(client);
		}
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		 if(channelPromise==null || channelPromise.isDone()) {
			 client.resultPromise().setFailure(cause);
		 }else {
			 channelPromise.setFailure(cause);
		 }
    }

}
