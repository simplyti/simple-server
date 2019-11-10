package com.simplyti.service.security.oidc;

public class GuiceOpenId {

	public static WellKnownOpenIdModule wellKnownOpenId(String endpoint) {
		return new WellKnownOpenIdModule(endpoint);
	}

}
