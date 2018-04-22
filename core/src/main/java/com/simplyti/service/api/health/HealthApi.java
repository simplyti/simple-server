package com.simplyti.service.api.health;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

public class HealthApi implements ApiProvider{
	

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/_health")
			.then(ctx->ctx.send("OK"));
	}

}
