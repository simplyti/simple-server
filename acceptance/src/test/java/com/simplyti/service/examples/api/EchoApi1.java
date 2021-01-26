package com.simplyti.service.examples.api;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

public class EchoApi1 implements ApiProvider {

	@Override
	public void build(ApiBuilder builder) {
		builder.when().post("/echo")
			.then(ctx->ctx.send(ctx.body().copy()));
		
		builder.when().post("/hello")
			.then(ctx->ctx.send("Hello from service post"));
		
		builder.when().put("/put1")
			.then(ctx->ctx.send("Hello PUT 1"));
	}

}
