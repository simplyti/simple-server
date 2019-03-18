package com.simplyti.service.api.builder;

import java.util.Collection;

import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.multipart.FileUpload;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;

public class TypeableRequestFinishableApiBuilder extends RawFinishableApiBuilder implements BodyLengthConfigurable<TypeableRequestFinishableApiBuilder>{

	private static final TypeLiteral<ByteBuf> BYTEBUF = new TypeLiteral<ByteBuf>() {};
	private static final TypeLiteral<Collection<FileUpload>> FILE_UPLOADS = new TypeLiteral<Collection<FileUpload>>() {};

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
	
	@Override
	public <O> TypableRequestTypedResponseFinishableApiBuilder<ByteBuf, O> withResponseBodyType(Class<O> responseType) {
		return new TypableRequestTypedResponseFinishableApiBuilder<>(builder,method,uri,BYTEBUF,maxBodyLength);
	}

	public TypedRequestFinishableApiBuilder<Collection<FileUpload>, Object> asFileUplod() {
		return new TypedRequestFinishableApiBuilder<>(builder,method,uri,FILE_UPLOADS, true, maxBodyLength);
	}

	@Override
	public TypeableRequestFinishableApiBuilder withMaximunBodyLength(int length) {
		maxBodyLength =length;
		return this;
	}
	
}
