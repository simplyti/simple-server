package com.simplyti.service.examples.api;

import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;

public class OtherAPITest implements ApiProvider{

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/other/hello")
			.then(ctx->ctx.send("Hello!"));
		
	}

}
