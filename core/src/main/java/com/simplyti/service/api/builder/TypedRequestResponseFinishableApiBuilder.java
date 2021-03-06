package com.simplyti.service.api.builder;

import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedRequestResponseFinishableApiBuilder<I, O> extends TypedRequestFinishableApiBuilder<I,O>{

	public TypedRequestResponseFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri,TypeLiteral<I> requestType, int maxBodyLength) {
		super(builder, method, uri,requestType,maxBodyLength);
	}

}
