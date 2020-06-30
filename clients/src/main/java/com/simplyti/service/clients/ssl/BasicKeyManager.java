package com.simplyti.service.clients.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

public class BasicKeyManager implements X509KeyManager {

	private static final String SELF = "self";
	
	private final PrivateKey key;
	private final X509Certificate[] certs;

	public BasicKeyManager(PrivateKey key, X509Certificate[] certs) {
		this.key=key;
		this.certs=certs;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return SELF;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return certs;
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return key;
	}
	
	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		throw new UnsupportedOperationException();
	}

	
}
