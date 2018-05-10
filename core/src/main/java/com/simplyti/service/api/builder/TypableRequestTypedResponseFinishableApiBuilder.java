package com.simplyti.service.api.builder;

import com.jsoniter.spi.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypableRequestTypedResponseFinishableApiBuilder<I, O> extends TypedResponseFinishableApiBuilder<I, O>{
	
	public TypableRequestTypedResponseFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType,
			int maxBodyLength) {
		super(builder, method, uri, requestType,maxBodyLength);
	}

	@SuppressWarnings("unchecked")
	public <II> TypedRequestResponseFinishableApiBuilder<II,O> withRequestBodyType(Class<II> requestType) {
		return new TypedRequestResponseFinishableApiBuilder<>(builder,method,uri,TypeLiteral.create(requestType),maxBodyLength);
	}
	
}
