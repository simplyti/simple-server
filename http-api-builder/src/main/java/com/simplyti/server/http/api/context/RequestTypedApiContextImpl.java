package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class RequestTypedApiContextImpl<T> extends RequestResponseTypedApiContextImpl<T,Object> implements RequestTypedApiContext<T> {

	public RequestTypedApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, Json json, TypeLiteral<T> requestType, HttpRequest request, ByteBuf body,
			ApiMatchRequest match) {
		super(syncTaskSubmitter, exceptionHandler,ctx, json, requestType, request, body,match);
	}

}
