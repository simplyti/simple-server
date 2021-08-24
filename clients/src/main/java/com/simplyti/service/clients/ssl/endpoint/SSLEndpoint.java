package com.simplyti.service.clients.ssl.endpoint;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import com.simplyti.service.clients.endpoint.Endpoint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper=true, of= {"certs"})
@Getter
@Accessors(fluent = true)
@SuperBuilder
public class SSLEndpoint extends Endpoint {

	private final List<X509Certificate> certs;
	private final PrivateKey key;

}

