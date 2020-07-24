package com.simplyti.service.ssl;

import java.security.cert.X509Certificate;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;

public class SelfSignedServerCertificateProvider implements DefaultServerCertificateProvider {
	
	private static final String NAME = "Simple Server";
	
	private ServerCertificate serverCertificate;

	@SneakyThrows
	@Override
	public ServerCertificate get(String alias) {
		if(serverCertificate == null) {
			 SelfSignedCertificate ssc = new SelfSignedCertificate(NAME);
			 serverCertificate = new ServerCertificate(new X509Certificate[] {ssc.cert()}, ssc.key());
		}
		return serverCertificate;
	}

}
