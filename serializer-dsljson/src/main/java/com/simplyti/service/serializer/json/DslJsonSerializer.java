package com.simplyti.service.serializer.json;

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
import lombok.SneakyThrows;


public class DslJsonSerializer implements Json {
	
	private DslJson<Object> dslJson;

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
		return (T) dslJson.deserialize(type.getType(), new ByteBufInputStream(content));
	}

	@Override
	@SneakyThrows
	public void serialize(Object obj, ByteBuf buffer) {
		dslJson.serialize(obj, new ByteBufOutputStream(buffer));
	}

}
