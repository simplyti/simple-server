package com.simplyti.service.serializer.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;

import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.SneakyThrows;

public class JacksonJson implements Json {
	
	private final ObjectMapper mapper;
	
	
	@Inject	
	public JacksonJson(Set<Module> modules) {
		this.mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		
		modules.forEach(mapper::registerModule);
	}

	@Override
	@SneakyThrows
	public <T> T deserialize(ByteBuf content, TypeLiteral<T> type) {
		if(content.isReadable()) {
			return mapper.readValue(new ByteBufInputStream(content), new TypeReferenceLiteral<T>(type.getType()));
		}else {
			return null;
		}
	}

	@Override
	@SneakyThrows
	public <T> T deserialize(byte[] data, TypeLiteral<T> type) {
		return mapper.readValue(data, new TypeReferenceLiteral<T>(type.getType()));
	}

	@Override
	@SneakyThrows
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return mapper.readValue(data, clazz);
	}

	@Override
	@SneakyThrows
	public <T> T deserialize(ByteBuf content, Class<T> clazz) {
		return mapper.readValue((InputStream) new ByteBufInputStream(content), clazz);
	}
	
	@Override
	@SneakyThrows
	public <T> T deserialize(String content, Class<T> clazz) {
		return mapper.readValue(content, clazz);
	}
	
	@Override
	@SneakyThrows
	public <T> T deserialize(String content, TypeLiteral<T> type) {
		return mapper.readValue(content, new TypeReferenceLiteral<T>(type.getType()));
	}

	@Override
	@SneakyThrows
	public void serialize(Object obj, ByteBuf buffer) {
		mapper.writeValue((OutputStream) new ByteBufOutputStream(buffer), obj);
	}

	@Override
	@SneakyThrows
	public byte[] serialize(Object obj) {
		return mapper.writeValueAsBytes(obj);
	}

	@Override
	@SneakyThrows
	public String serializeAsString(Object obj, Charset charset) {
		return mapper.writeValueAsString(obj);
	}

}
