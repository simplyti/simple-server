package com.simplyti.service.api.builder;

import com.jsoniter.spi.TypeLiteral;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;

public class RawFinishableApiBuilder extends FinishableApiBuilder<ByteBuf,Object> {
	
	private static final TypeLiteral<ByteBuf> RAW_TYPE = new TypeLiteral<ByteBuf>(){};
	
	public RawFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri) {
		super(builder, method, uri, RAW_TYPE, false, -1);
	}

	@SuppressWarnings("unchecked")
	public <O> TypedResponseFinishableApiBuilder<ByteBuf, O> withResponseBodyType(TypeLiteral<O> responseType) {
		return new TypedResponseFinishableApiBuilder<>(builder,method,uri,TypeLiteral.create(ByteBuf.class),maxBodyLength);
	}

	@SuppressWarnings("unchecked")
	public <O> TypedResponseFinishableApiBuilder<ByteBuf, O> withResponseBodyType(Class<O> responseType) {
		return withResponseBodyType(TypeLiteral.create(responseType));
	}
	
	public <O> StreamedApiBuilder streamedInput() {
		return new StreamedApiBuilder(builder,method,uri);
	}

}
