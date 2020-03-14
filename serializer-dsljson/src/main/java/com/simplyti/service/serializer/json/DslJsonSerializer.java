package com.simplyti.service.serializer.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import com.dslplatform.json.Configuration;
import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;

public class DslJsonSerializer implements Json {
	
	private DslJson<Object> dslJson;
	
	public DslJsonSerializer() {
		this(Collections.emptySet());
	}

	@Inject
	public DslJsonSerializer(Set<Configuration> configurations) {
		com.dslplatform.json.DslJson.Settings<Object> settings = Settings
				.withRuntime()
				.skipDefaultValues(true)
				.allowArrayFormat(true)
				.includeServiceLoader();
		configurations.forEach(settings::with);
		this.dslJson = new DslJson<Object>(settings);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SneakyThrows
	public <T> T deserialize(ByteBuf content, TypeLiteral<T> type) {
		if(content.isReadable()) {
			return (T) dslJson.deserialize(type.getType(), new ByteBufInputStream(content));
		} else {
			return null;
		}
	}
	
	@Override
	@SneakyThrows
	public <T> T deserialize(ByteBuf content, Class<T> clazz) {
		return dslJson.deserialize(clazz, new ByteBufInputStream(content));
	}
	
	@Override
	@SneakyThrows
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return dslJson.deserialize(clazz, new ByteArrayInputStream(data));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SneakyThrows
	public <T> T deserialize(byte[] data, TypeLiteral<T> type) {
		return (T) dslJson.deserialize(type.getType(), new ByteArrayInputStream(data));
	}
	
	@Override
	@SneakyThrows
	public <T> T deserialize(String content, Class<T> clazz) {
		return dslJson.deserialize(clazz, new ByteArrayInputStream(content.getBytes(CharsetUtil.UTF_8)));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SneakyThrows
	public <T> T deserialize(String content, TypeLiteral<T> type) {
		return (T) dslJson.deserialize(type.getType(), new ByteArrayInputStream(content.getBytes(CharsetUtil.UTF_8)));
	}

	@Override
	@SneakyThrows
	public void serialize(Object obj, ByteBuf buffer) {
		dslJson.serialize(obj, new ByteBufOutputStream(buffer));
	}
	
	@Override
	@SneakyThrows
	public String serializeAsString(Object obj, Charset charset) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		dslJson.serialize(obj, os);
		return os.toString(charset.name());
	}


	@Override
	@SneakyThrows
	public byte[] serialize(Object obj) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		dslJson.serialize(obj, os);
		return os.toByteArray();
	}

}
