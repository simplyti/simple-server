package com.simplyti.service.examples.api;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

public class EchoApi2 implements ApiProvider {

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/echo")
			.then(ctx->ctx.send("Hello GET"));

		builder.when().get("/hello")
			.then(ctx->ctx.send("Hello from service get"));
		
		builder.when().put("/put2")
			.then(ctx->ctx.send("Hello PUT 2"));
	}

}
