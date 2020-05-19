package com.simplyti.service.clients.http.handler;

import java.util.function.Function;

import com.simplyti.service.clients.ClientRequestChannel;

import io.netty.handler.codec.http.FullHttpResponse;

public class DecodingFullHttpResponseHandler<T> extends FullHttpResponseHandler<T> {

	private final Function<FullHttpResponse, T> function;


	public DecodingFullHttpResponseHandler(Function<FullHttpResponse,T> function, ClientRequestChannel<T> clientChannel, int maxContentLength, boolean checkStatusCode) {
		super(clientChannel,maxContentLength, checkStatusCode);
		this.function=function;
	}
	
	protected T handle(FullHttpResponse msg) {
		return function.apply(msg);
	}
	
}
