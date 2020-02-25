package com.simplyti.service.examples.api;

import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;

public class MaximunBodyTestApi implements ApiProvider {

	@Override
	public void build(ApiBuilder builder) {
		builder.when().post("/errorrfcount")
			.then(ctx->ctx.send(ctx.body()));
		
		builder.when().post("/maximun10")
			.withMaximunBodyLength(10)
			.then(ctx->ctx.send(ctx.body().copy()));
		
	}

}
