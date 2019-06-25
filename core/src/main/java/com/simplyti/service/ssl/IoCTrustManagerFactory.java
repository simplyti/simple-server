package com.simplyti.service.ssl;

import java.security.Provider;

import javax.inject.Inject;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManagerFactorySpi;

public class IoCTrustManagerFactory extends TrustManagerFactory {

	@Inject
	public IoCTrustManagerFactory(TrustManagerFactorySpi spi, Provider provider) {
		super(spi, provider, provider.getName());
	}

}
