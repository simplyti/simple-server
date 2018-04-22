package com.simplyti.service.ssl.sni;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class ServerCertificate {
	
	private final X509Certificate[] certificateChain;
	private final PrivateKey privateKey;
	
	public ServerCertificate(X509Certificate[] certificateChain, PrivateKey privateKey) {
		this.certificateChain=certificateChain;
		this.privateKey=privateKey;
	}

	public X509Certificate[] certificateChain() {
		return certificateChain;
	}

	public PrivateKey privateKey() {
		return privateKey;
	}

}
