package com.simplyti.service.ssl;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;


public class IoCTrustManager extends X509ExtendedTrustManager {

	private static final X509Certificate[] EMPTY = {};
	
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return EMPTY;
	}
	
	@Override
	public void checkClientTrusted(X509Certificate[] certificates, String algorithm, SSLEngine engine) throws CertificateException {
		algorithm.toCharArray();
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2) throws CertificateException {
		throw new UnsupportedOperationException();
	}

}
