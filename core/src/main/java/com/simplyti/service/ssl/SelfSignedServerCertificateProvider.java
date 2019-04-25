package com.simplyti.service.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import io.netty.handler.ssl.util.SelfSignedCertificate;

public class SelfSignedServerCertificateProvider implements DefaultServerCertificateProvider {
	
	private static final String NAME = "Simple Server";
	
	private final ServerCertificate serverCertificate;

	public SelfSignedServerCertificateProvider() throws CertificateException {
		SelfSignedCertificate ssc = new SelfSignedCertificate(NAME);
		this.serverCertificate = new ServerCertificate(new X509Certificate[] {ssc.cert()}, ssc.key());
	}

	@Override
	public ServerCertificate get(String alias) {
		return serverCertificate;
	}

}
