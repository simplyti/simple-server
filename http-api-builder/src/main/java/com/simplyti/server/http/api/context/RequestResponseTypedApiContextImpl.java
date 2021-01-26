package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class RequestResponseTypedApiContextImpl<T,U> extends AbstractWithBodyApiContext<U> implements RequestTypedApiContext<T> , RequestResponseTypedApiContext<T, U> {

	private final TypeLiteral<T> requestType;
	private final ByteBuf body;
	private final Json json;
	
	private boolean bodyParsed;
	private T bodyObj;

	public RequestResponseTypedApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, Json json, TypeLiteral<T> requestType, HttpRequest request, ByteBuf body,
			ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx, request, match, exceptionHandler, body);
		this.json=json;
		this.requestType=requestType;
		this.body=body;
	}

	@Override
	public T body() {
		if(bodyParsed) {
			return this.bodyObj;
		}
		if(requestType.getType().equals(Void.class)) {
			this.bodyObj = null;
		} else {
			this.bodyObj = json.deserialize(body,requestType);
		}
		this.bodyParsed=true;
		release();
		return bodyObj;
	}

}
