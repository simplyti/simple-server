package com.simplyti.service.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class ServerCertificate {
	
	private final X509Certificate[] certificateChain;
	private final PrivateKey privateKey;
	
	public ServerCertificate(X509Certificate[] certificateChain, PrivateKey privateKey) {
		this.certificateChain=Arrays.copyOf(certificateChain,certificateChain.length);;
		this.privateKey=privateKey;
	}

	public X509Certificate[] certificateChain() {
		return Arrays.copyOf(certificateChain,certificateChain.length);
	}

	public PrivateKey privateKey() {
		return privateKey;
	}

}
