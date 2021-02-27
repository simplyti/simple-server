package com.simplyti.service.clients.http.handler;

import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;

public class DecodingFullHttpResponseHandler<T> extends AbstractFullHttpResponseHandler<T> {

	private final Function<FullHttpResponse, T> fn;

	public DecodingFullHttpResponseHandler(ClientChannel channel, ByteBuf buff, boolean checkStatus, Promise<T> promise, Function<FullHttpResponse, T> fn) {
		super(channel, buff, promise, checkStatus);
		this.fn=fn;
	}

	@Override
	protected T handle(FullHttpResponse msg) {
		try {
			return fn.apply(msg);
		} finally {
			msg.release();
		}
	}

}
