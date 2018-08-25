package com.simplyti.service.security.oidc.jwk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.cert.CertificateFactory;
import java.util.Base64;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.spi.Decoder;

import io.vavr.control.Try;

public class JsonWebKeyDecoder implements Decoder {

	@Override
	public Object decode(JsonIterator json) throws IOException {
		Any any = json.readAny();
		Key key = null;
		Any x5cList = any.get("x5c");
		if(x5cList.valueType()==ValueType.ARRAY) {
			Any x5c = x5cList.asList().get(0);
			key = Try.of(()->Try.of(()->Try.of(()->CertificateFactory.getInstance("X.509")).get()).get()
					.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(x5c.toString()))))
					.get().getPublicKey();
		}
		return new JsonWebKey(any.get("alg").toString(), any.get("kid").toString(), key);
	}

}
