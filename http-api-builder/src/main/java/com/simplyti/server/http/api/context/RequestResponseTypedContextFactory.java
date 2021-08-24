package com.simplyti.server.http.api.context;

import com.simplyti.server.http.api.operations.RequestResponseTypeApiOperation;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class RequestResponseTypedContextFactory implements ApiContextFactory {
	
	private final Json json;

	public RequestResponseTypedContextFactory(Json json) {
		this.json=json;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ApiContext> T create(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, ApiMatchRequest match, HttpRequest request, ByteBuf body) {
		RequestResponseTypeApiOperation<?,?> requestOperation = (RequestResponseTypeApiOperation<?,?>) match.operation();
		return (T) new RequestResponseTypedApiContextImpl<>(syncTaskSubmitter, exceptionHandler, ctx,json,requestOperation.requestType(),request,body,match);
	}

}
