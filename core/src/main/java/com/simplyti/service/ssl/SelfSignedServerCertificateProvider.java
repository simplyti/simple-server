package com.simplyti.service.ssl;

import java.security.cert.X509Certificate;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;

public class SelfSignedServerCertificateProvider implements DefaultServerCertificateProvider, Supplier<ServerCertificate> {
	
	private static final String NAME = "Simple Server";
	
	private final Supplier<ServerCertificate> serverCertificate = Suppliers.memoize(this);

	@Override
	public ServerCertificate get(String alias) {
		return serverCertificate.get();
	}

	@SneakyThrows
	@Override
	public ServerCertificate get() {
		SelfSignedCertificate ssc = new SelfSignedCertificate(NAME);
		return new ServerCertificate(new X509Certificate[] {ssc.cert()}, ssc.key());
	}

}
