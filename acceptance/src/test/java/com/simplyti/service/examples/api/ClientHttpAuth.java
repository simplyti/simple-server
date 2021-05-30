package com.simplyti.service.examples.api;

import java.util.Base64;
import java.util.Collections;

import com.jsoniter.output.JsonStream;
import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.exception.UnauthorizedException;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;

public class ClientHttpAuth implements ApiProvider {

	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/basic-auth/{user}/{pass}")
			.then(ctx->basicAuth("Hello", ctx));
		
		builder.when().post("/basic-auth/{user}/{pass}")
			.then(ctx->basicAuth(ctx.body().toString(CharsetUtil.UTF_8), ctx));
		
		builder.when().get("/bearer-auth/{user}")
			.then(ctx->bearerAuth("Hello", ctx));
		
		builder.when().post("/bearer-auth/{user}")
			.then(ctx->bearerAuth(ctx.body().toString(CharsetUtil.UTF_8), ctx));
		
	}
	
	private void basicAuth(String prefix, AnyApiContext ctx) {
		String auth = ctx.request().headers().get(HttpHeaderNames.AUTHORIZATION);
		String expectedAuth = "Basic " + Base64.getEncoder().encodeToString((ctx.pathParam("user")+":"+ctx.pathParam("pass")).getBytes());
		if(auth !=null && auth.equals(expectedAuth)) {
			ctx.send(prefix+" "+ctx.pathParam("user"));
		} else {
			ctx.failure(new UnauthorizedException());
		}
	}
	
	private void bearerAuth(String prefix, AnyApiContext ctx) {
		String auth = ctx.request().headers().get(HttpHeaderNames.AUTHORIZATION);
		String expectedAuth = "Bearer " + Base64.getEncoder().encodeToString(JsonStream.serialize(Collections.singletonMap("name", ctx.pathParam("user"))).getBytes());
		if(auth !=null && auth.equals(expectedAuth)) {
			ctx.send(prefix+" "+ctx.pathParam("user"));
		} else {
			ctx.failure(new UnauthorizedException());
		}
	}

}
