package com.simplyti.service.clients.endpoint.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import com.simplyti.service.clients.Schema;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.Endpoint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper=true, of= {"name"})
@Getter
@Accessors(fluent = true)
public class SSLEndpoint extends Endpoint {

	private final PrivateKey key;
	private final X509Certificate[] certs;
	private final String name;

	public SSLEndpoint(Schema schema, Address address, PrivateKey key, List<X509Certificate> certs) {
		super(schema, address);
		this.key=key;
		this.certs=certs.toArray(new X509Certificate[0]);
		this.name=this.certs[0].getSubjectDN().getName();
	}

}

