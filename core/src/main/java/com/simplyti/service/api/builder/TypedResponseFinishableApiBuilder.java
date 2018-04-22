package com.simplyti.service.api.builder;

import com.jsoniter.spi.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedResponseFinishableApiBuilder<I, O> extends FinishableApiBuilder<I,O>{

	public TypedResponseFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType) {
		super(builder, method, uri, requestType, false);
	}


}
