package com.simplyti.service.clients.k8s.json.coder;

import java.io.IOException;
import java.util.Base64;

import com.dslplatform.json.JsonConverter;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.simplyti.service.clients.k8s.secrets.domain.SecretData;

import io.netty.util.CharsetUtil;

@JsonConverter(target = SecretData.class)
public class SecretDataConverter {
	
	public static final JsonReader.ReadObject<SecretData> JSON_READER = new JsonReader.ReadObject<SecretData>() {
		public SecretData read(@SuppressWarnings("rawtypes") JsonReader reader) throws IOException {
			return SecretData.of(Base64.getDecoder().decode(reader.readString().getBytes(CharsetUtil.UTF_8)));
		}
	};
	
	public static final JsonWriter.WriteObject<SecretData> JSON_WRITER = new JsonWriter.WriteObject<SecretData>() {
		public void write(JsonWriter writer, SecretData value) {
			writer.writeString(Base64.getEncoder().encodeToString(value.getData()));
		}
	};

}
