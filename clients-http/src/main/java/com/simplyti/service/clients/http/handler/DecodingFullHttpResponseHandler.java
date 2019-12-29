package com.simplyti.service.clients.http.handler;

import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;

public class DecodingFullHttpResponseHandler<T> extends AbstractFullHttpResponseHandler<T> {

	private final Function<FullHttpResponse, T> fn;

	public DecodingFullHttpResponseHandler(String handlerName, ClientChannel channel, Promise<T> promise, boolean checkStatus, Function<FullHttpResponse, T> fn) {
		super(handlerName, channel, promise, checkStatus);
		this.fn=fn;
	}

	@Override
	protected T handle(FullHttpResponse msg) {
		T result = fn.apply(msg);
		msg.release();
		return result;
	}

}
