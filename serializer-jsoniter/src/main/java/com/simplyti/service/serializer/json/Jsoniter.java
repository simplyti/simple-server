package com.simplyti.service.serializer.json;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import static com.jsoniter.spi.TypeLiteral.create;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class Jsoniter implements Json {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(ByteBuf content, TypeLiteral<T> type) {
		byte[] data = new byte[content.readableBytes()];
		content.readBytes(data);
		return (T) JsonIterator.deserialize(data,create(type.getType()));
	}
	
	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return JsonIterator.deserialize(data, clazz);
	}

	@Override
	public <T> T deserialize(ByteBuf content, Class<T> clazz) {
		byte[] data = new byte[content.readableBytes()];
		content.readBytes(data);
		return (T) JsonIterator.deserialize(data,clazz);
	}

	@Override
	public void serialize(Object obj, ByteBuf buffer) {
		JsonStream.serialize(obj,new ByteBufOutputStream(buffer));
	}


	@Override
	public byte[] serialize(Object obj) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JsonStream.serialize(obj, os);
		return os.toByteArray();
	}

	@Override
	public String serializeAsString(Object obj, Charset charset) {
		return JsonStream.serialize(obj);
	}


}
