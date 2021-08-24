package com.simplyti.service.clients.http.handler;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;

public class FullHttpResponseHandler extends AbstractFullHttpResponseHandler<FullHttpResponse> {

	public FullHttpResponseHandler(ClientChannel channel, ByteBuf buff, boolean checkStatus, Promise<FullHttpResponse> promise) {
		super(channel, buff, promise, checkStatus);
	}

	@Override
	protected FullHttpResponse handle(FullHttpResponse msg) {
		return msg;
	}

}
