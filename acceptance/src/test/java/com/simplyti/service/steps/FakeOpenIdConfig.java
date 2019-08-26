package com.simplyti.service.steps;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
public class FakeOpenIdConfig {
	
	private final SelfSignedCertificate key;
	private final String tokenEndpoint;
	private final String authEndpoint;
	private final int wellKnownDelay;
	private final int jwksDelay;

}
