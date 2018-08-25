package com.simplyti.service;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.security.oidc.OpenIdModule;

public class OidcTestApi implements ApiProvider{

	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/hello")
			.withMeta(OpenIdModule.META_ATT,"true")
			.then(ctx -> ctx.send("Hello OIDC!"));
		
		builder.when().get("/noauth/hello")
			.then(ctx -> ctx.send("Hello!"));
		
	}

}
