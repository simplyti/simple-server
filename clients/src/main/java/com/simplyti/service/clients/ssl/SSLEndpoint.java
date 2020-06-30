package com.simplyti.service.clients.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.X509KeyManager;

import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.Schema;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper=true, of= {"name"})
@Getter
@Accessors(fluent = true)
public class SSLEndpoint extends Endpoint {

	private final String name;
	private final X509KeyManager keyManager;

	public SSLEndpoint(Schema schema, Address address, PrivateKey key, List<X509Certificate> certs) {
		super(schema, address);
		X509Certificate[] certArray = certs.toArray(new X509Certificate[0]);
		this.keyManager=new BasicKeyManager(key,certArray);
		this.name=certArray[0].getSubjectDN().getName();
	}

}
