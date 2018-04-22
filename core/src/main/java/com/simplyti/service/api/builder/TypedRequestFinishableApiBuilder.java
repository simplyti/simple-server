package com.simplyti.service.api.builder;

import com.jsoniter.spi.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedRequestFinishableApiBuilder<I,O> extends FinishableApiBuilder<I,O>{
	
	private final TypeLiteral<I> requestType;
	
	public TypedRequestFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri,TypeLiteral<I> requestType) {
		this(builder, method, uri, requestType, false);
	}

	public TypedRequestFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri,TypeLiteral<I> requestType, boolean multipart) {
		super(builder, method, uri, requestType,multipart);
		this.requestType=requestType;
	}

	public <OO> TypedRequestResponseFinishableApiBuilder<I,OO> withResponseBodyType(Class<OO> responseType) {
		return new TypedRequestResponseFinishableApiBuilder<I,OO>(builder, method, uri,requestType);
	}

}
