package com.simplyti.service;


import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

public class OtherAPITest implements ApiProvider{

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/other/hello")
			.then(ctx->ctx.send("Hello!"));
		
	}

}
