package com.simplyti.service.serializer.json;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import static com.jsoniter.spi.TypeLiteral.create;

public class Jsoniter implements Json {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(ByteBuf content, TypeLiteral<T> type) {
		byte[] data = new byte[content.readableBytes()];
		content.readBytes(data);
		return (T) JsonIterator.deserialize(data,create(type.getType()));
	}

	@Override
	public void serialize(Object obj, ByteBuf buffer) {
		JsonStream.serialize(obj,new ByteBufOutputStream(buffer));
	}


}
