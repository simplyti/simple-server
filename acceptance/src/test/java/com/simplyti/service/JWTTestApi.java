package com.simplyti.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.simplyti.service.api.APIContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.auth.RequiresAuth;

@Path("jaxrs")
public class JWTTestApi implements ApiProvider {

	@Override
	public void build(ApiBuilder builder) {

		builder.when().get("/hello")
			.withRequiresAuth()
				.then(ctx -> ctx.send("Hello!"));

		builder.when().get("/noauth/hello")
			.then(ctx -> ctx.send("Hello!"));
		
		builder.usingJaxRSContract(JWTTestApi.class);
	}

	@GET
	@Path("/hello")
	@RequiresAuth
	public void getHello(APIContext<String> ctx) {
		ctx.send("Hello!");
	}
	
}
