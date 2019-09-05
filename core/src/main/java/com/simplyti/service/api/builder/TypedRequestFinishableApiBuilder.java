package com.simplyti.service.api.builder;

import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

public class TypedRequestFinishableApiBuilder<I,O> extends FinishableApiBuilder<I,O>{
	
	private final TypeLiteral<I> requestType;
	
	public TypedRequestFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri,TypeLiteral<I> requestType, int maxBodyLength) {
		this(builder, method, uri, requestType, false,maxBodyLength);
	}

	public TypedRequestFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri,TypeLiteral<I> requestType, boolean multipart, int maxBodyLength) {
		super(builder, method, uri, requestType,multipart,maxBodyLength,false);
		this.requestType=requestType;
	}

	public <OO> TypedRequestResponseFinishableApiBuilder<I,OO> withResponseBodyType(Class<OO> responseType) {
		return new TypedRequestResponseFinishableApiBuilder<I,OO>(builder, method, uri,requestType,maxBodyLength);
	}

}
