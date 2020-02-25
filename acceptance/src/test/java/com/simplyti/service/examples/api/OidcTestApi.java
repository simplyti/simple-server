package com.simplyti.service.examples.api;

import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;
import com.simplyti.service.security.oidc.filter.OpenIdOperationFilter;

public class OidcTestApi implements ApiProvider{

	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/hello")
			.withMeta(OpenIdOperationFilter.META_ATT,"true")
			.then(ctx -> ctx.send("Hello OIDC!"));
		
		builder.when().get("/noauth/hello")
			.then(ctx -> ctx.send("Hello!"));
		
	}

}
