package com.simplyti.service.security.oidc.jwk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dslplatform.json.Configuration;
import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonReader.ReadObject;
import com.dslplatform.json.ObjectConverter;

import lombok.SneakyThrows;

public class JsonWebKeyConfiguration implements Configuration, ReadObject<JsonWebKeys> {
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure(@SuppressWarnings("rawtypes") DslJson json) {
		json.registerReader(JsonWebKeys.class, this);
	}

	@Override
	public JsonWebKeys read(@SuppressWarnings("rawtypes") JsonReader reader) throws IOException {
		Map<String, Object> map = ObjectConverter.deserializeMap(reader);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> keys = (List<Map<String, Object>>) map.get("keys");
		List<JsonWebKey> list = keys.stream()
			.map(item->{
				List<?> x5cList = (List<?>) item.get("x5c");
				Key key = null;
				if(x5cList!=null) {
					Object x5c = x5cList.get(0);
					key = certificate(x5c);
				}
				return new JsonWebKey(item.get("alg").toString(), item.get("kid").toString(), key);
			}).collect(Collectors.toList());
		return new JsonWebKeys(list);
	}

	@SneakyThrows
	private Key certificate(Object x5c) {
		 return CertificateFactory.getInstance("X.509")
			.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(x5c.toString())))
			.getPublicKey();
	}
	
}
