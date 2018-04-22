package com.simplyti.service.ssl;

import java.security.Provider;

import javax.inject.Inject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;

public class IoCKeyManagerFactory extends KeyManagerFactory {

	@Inject
	public IoCKeyManagerFactory(KeyManagerFactorySpi spi,Provider provider) {
		super(spi, provider, provider.getName());
	}

}
