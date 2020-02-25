package com.simplyti.server.http.api.health;

import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;

public class HealthApi implements ApiProvider{
	
	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/_health")
			.then(ctx->ctx.send("OK"));
	}

}
