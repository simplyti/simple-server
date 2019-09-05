package com.simplyti.service.api.builder;

import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedResponseFinishableApiBuilder<I, O> extends FinishableApiBuilder<I,O>{

	public TypedResponseFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri, TypeLiteral<I> requestType,
			int maxBodyLength) {
		super(builder, method, uri, requestType, false, maxBodyLength,false);
	}


}
