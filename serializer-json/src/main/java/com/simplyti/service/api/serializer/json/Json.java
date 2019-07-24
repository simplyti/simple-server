package com.simplyti.service.api.serializer.json;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

public interface Json {

	<T> T deserialize(ByteBuf content, TypeLiteral<T> type);
	<T> T deserialize(byte[] data, Class<T> clazz);
	<T> T deserialize(ByteBuf content, Class<T> clazz);

	void serialize(Object obj, ByteBuf buffer);
	byte[] serialize(Object obj);
	String serializeAsString(Object obj, Charset charset);


}
