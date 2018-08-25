package com.simplyti.service.security.oidc.config.auto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class DefaultAutodiscoveredOpenIdConfig implements AutodiscoveredOpenIdConfig{

	private final String callbackUri;
	private final String cipherKey;

}
