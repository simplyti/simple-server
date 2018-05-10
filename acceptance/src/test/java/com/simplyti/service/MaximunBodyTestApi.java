package com.simplyti.service;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

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
