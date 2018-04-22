package com.simplyti.service.api.builder;

import com.jsoniter.spi.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedRequestResponseFinishableApiBuilder<I, O> extends TypedRequestFinishableApiBuilder<I,O>{

	public TypedRequestResponseFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri,TypeLiteral<I> requestType) {
		super(builder, method, uri,requestType);
	}

}
