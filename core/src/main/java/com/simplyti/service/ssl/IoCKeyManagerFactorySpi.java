package com.simplyti.service.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;

public class IoCKeyManagerFactorySpi extends KeyManagerFactorySpi {

	private final KeyManager[] keyManagers;

	@Inject
	public IoCKeyManagerFactorySpi(KeyManager keyManagers) {
		this.keyManagers = new KeyManager[] {keyManagers};
	}

	@Override
	protected KeyManager[] engineGetKeyManagers() {
		return keyManagers;
	}

	@Override
	protected void engineInit(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected void engineInit(KeyStore ks, char[] password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
		throw new UnsupportedOperationException();
	}

}
