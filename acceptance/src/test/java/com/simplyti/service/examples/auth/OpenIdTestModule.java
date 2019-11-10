package com.simplyti.service.examples.auth;

import com.google.inject.AbstractModule;
import com.simplyti.service.security.oidc.GuiceOpenId;

public class OpenIdTestModule extends AbstractModule {
	
	private final String wellKnownAddress;

	public OpenIdTestModule(String wellKnownAddress) {
		this.wellKnownAddress=wellKnownAddress;
	}
	
	@Override
	public void configure() {
		install(GuiceOpenId.wellKnownOpenId(wellKnownAddress));
	}

}
