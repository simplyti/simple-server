package com.simplyti.service.api.builder;

import java.util.Collection;

import com.google.inject.util.Types;
import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.multipart.FileUpload;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;

public class TypeableRequestFinishableApiBuilder extends RawFinishableApiBuilder implements BodyLengthConfigurable<TypeableRequestFinishableApiBuilder>{

	public TypeableRequestFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri) {
		super(builder, method, uri);
	}
	
	@SuppressWarnings("unchecked")
	public <T> TypedRequestFinishableApiBuilder<T,Object> withRequestBodyType(Class<T> clazz) {
		return withRequestBodyType(TypeLiteral.create(clazz));
	}
	
	public <T> TypedRequestFinishableApiBuilder<T,Object> withRequestBodyType(TypeLiteral<T> type) {
		return new TypedRequestFinishableApiBuilder<>(builder,method,uri,type, maxBodyLength);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <O> TypableRequestTypedResponseFinishableApiBuilder<ByteBuf, O> withResponseBodyType(Class<O> responseType) {
		return new TypableRequestTypedResponseFinishableApiBuilder<>(builder,method,uri,TypeLiteral.create(ByteBuf.class),maxBodyLength);
	}

	@SuppressWarnings("unchecked")
	public TypedRequestFinishableApiBuilder<Collection<FileUpload>, Object> asFileUplod() {
		return new TypedRequestFinishableApiBuilder<>(builder,method,uri,TypeLiteral.create(Types.collectionOf(FileUpload.class)),
				true, maxBodyLength);
	}

	@Override
	public TypeableRequestFinishableApiBuilder withMaximunBodyLength(int length) {
		maxBodyLength =length;
		return this;
	}
	
}