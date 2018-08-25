package com.simplyti.service.security.oidc.config.auto;

import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.security.oidc.config.OpenIdClientConfig;

public interface FullAutodiscoveredOpenIdConfig extends AutodiscoveredOpenIdConfig, OpenIdClientConfig {

	HttpEndpoint endpoint();
}
