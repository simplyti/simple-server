package com.simplyti.service.api.builder;

import java.util.Collection;

import com.google.inject.util.Types;
import com.jsoniter.spi.TypeLiteral;
import com.simplyti.service.api.multipart.FileUpload;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;

public class TypeableRequestFinishableApiBuilder extends RawFinishableApiBuilder{

	public TypeableRequestFinishableApiBuilder(ApiBuilder builder, HttpMethod method, String uri) {
		super(builder, method, uri);
	}
	
	@SuppressWarnings("unchecked")
	public <T> TypedRequestFinishableApiBuilder<T,Object> withRequestBodyType(Class<T> clazz) {
		return withRequestBodyType(TypeLiteral.create(clazz));
	}
	
	public <T> TypedRequestFinishableApiBuilder<T,Object> withRequestBodyType(TypeLiteral<T> type) {
		return new TypedRequestFinishableApiBuilder<T,Object>(builder,method,uri,type);
	}
	
	@SuppressWarnings("unchecked")
	public <OO> TypableRequestTypedResponseFinishableApiBuilder<ByteBuf, OO> withResponseBodyType(Class<OO> responseType) {
		return new TypableRequestTypedResponseFinishableApiBuilder<ByteBuf,OO>(builder,method,uri,TypeLiteral.create(ByteBuf.class));
	}

	@SuppressWarnings("unchecked")
	public TypedRequestFinishableApiBuilder<Collection<FileUpload>, Object> asFileUplod() {
		return new TypedRequestFinishableApiBuilder<Collection<FileUpload>,Object>(builder,method,uri,TypeLiteral.create(Types.collectionOf(FileUpload.class)),true);
	}

}
