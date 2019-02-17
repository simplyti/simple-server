package com.simplyti.service.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.inject.Inject;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;

public class IoCTrustManagerFactorySpi extends TrustManagerFactorySpi {
	
	private final TrustManager[] trustManagers;

	@Inject
	public IoCTrustManagerFactorySpi(TrustManager trustManager) {
		this.trustManagers = new TrustManager[] {trustManager};
	}

	@Override
	protected TrustManager[] engineGetTrustManagers() {
		return trustManagers;
	}

	@Override
	protected void engineInit(KeyStore ks) throws KeyStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void engineInit(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
		throw new UnsupportedOperationException();
	}

}
