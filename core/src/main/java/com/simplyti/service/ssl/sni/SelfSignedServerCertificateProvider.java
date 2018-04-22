package com.simplyti.service.ssl.sni;

import java.security.cert.X509Certificate;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.vavr.control.Try;

public class SelfSignedServerCertificateProvider implements DefaultServerCertificateProvider {
	
	private static final String NAME = "Simple Server";
	
	private final ServerCertificate serverCertificate;

	public SelfSignedServerCertificateProvider() {
		SelfSignedCertificate ssc = Try.of(()->new SelfSignedCertificate(NAME)).get();
		this.serverCertificate = new ServerCertificate(new X509Certificate[] {ssc.cert()}, ssc.key());
	}

	@Override
	public ServerCertificate get(String alias) {
		return serverCertificate;
	}

}
