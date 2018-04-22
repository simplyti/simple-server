package com.simplyti.service.api.builder;

import com.jsoniter.spi.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypableRequestTypedResponseFinishableApiBuilder<I, O> extends TypedResponseFinishableApiBuilder<I, O>{
	
	public TypableRequestTypedResponseFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType) {
		super(builder, method, uri, requestType);
	}

	@SuppressWarnings("unchecked")
	public <II> TypedRequestResponseFinishableApiBuilder<II,O> withRequestBodyType(Class<II> requestType) {
		return new TypedRequestResponseFinishableApiBuilder<II,O>(builder,method,uri,TypeLiteral.create(requestType));
	}
	
}
