package com.simplyti.server.http.api.builder.jaxrs;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class JaxRsApiContextFactory implements ApiContextFactory {
	
	private final Json json;

	@Inject
	public JaxRsApiContextFactory(Json json) {
		this.json=json;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ApiContext> T create(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler,
			ChannelHandlerContext ctx, ApiMatchRequest match, HttpRequest request, ByteBuf body) {
		MethodInvocationOperation<?> operation = (MethodInvocationOperation<?>) match.operation();
		Object obj = json.deserialize(body, operation.requestType());
		body.release();
		return (T) new DefaultJaxRsApiContext<>(syncTaskSubmitter, exceptionHandler, ctx, request, match, obj);
	}

}
