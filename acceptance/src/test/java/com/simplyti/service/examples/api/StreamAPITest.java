package com.simplyti.service.examples.api;


import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

public class StreamAPITest implements ApiProvider{
	
	@Override
	public void build(ApiBuilder builder) {
		builder.when().post("/echo/chunked")
		.withStreamedInput()
		.then(ctx->
			ctx.sendChunked(chunked->
				ctx.stream(data->chunked.send(data.retain()))
				.thenAccept(v->chunked.finish())));
		
		builder.when().post("/streamed/error")
			.withStreamedInput()
			.then(ctx->ctx.failure(new RuntimeException("This is an error")));
	}

}
