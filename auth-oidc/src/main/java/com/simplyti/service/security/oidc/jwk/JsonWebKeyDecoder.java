package com.simplyti.service.security.oidc.jwk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.spi.Decoder;

public class JsonWebKeyDecoder implements Decoder {

	@Override
	public Object decode(JsonIterator json) throws IOException {
		Any any = json.readAny();
		Key key = null;
		Any x5cList = any.get("x5c");
		if(x5cList.valueType()==ValueType.ARRAY) {
			Any x5c = x5cList.asList().get(0);
			try {
				key = CertificateFactory.getInstance("X.509")
						.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(x5c.toString())))
						.getPublicKey();
			} catch (CertificateException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return new JsonWebKey(any.get("alg").toString(), any.get("kid").toString(), key);
	}

}
