package com.simplyti.service.clients.k8s.json.coder;

import java.io.IOException;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.ObjectConverter;

import lombok.SneakyThrows;

public class AnyConverter {

	public static final JsonReader.ReadObject<Object> JSON_READER = new JsonReader.ReadObject<Object>() {
		public Object read(@SuppressWarnings("rawtypes") JsonReader reader) throws IOException {
			return ObjectConverter.deserializeObject(reader);
		}
	};
	
	public static final JsonWriter.WriteObject<Object> JSON_WRITER = new JsonWriter.WriteObject<Object>() {
		@SneakyThrows
		public void write(JsonWriter writer, Object value) {
			ObjectConverter.serializeObject(value, writer);
		}
	};
	
}
