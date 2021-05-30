package com.simplyti.service.examples.api;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.chunked.ChunkedResponseContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.util.concurrent.Future;

import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class APIChunkExample implements ApiProvider {
	
	private final Json json;

	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/chunked")
			.then(ctx->ctx.sendChunked(chunked->sendMessages(chunked, 1, ctx.queryParamAsInt("count"), ctx.queryParaAsBoolean("json"))
						.thenAccept(f->chunked.finish())));
		
		builder.when().post("/echo/chunked")
			.withStreamedInput()
			.then(ctx->
				ctx.sendChunked(chunked->
					ctx.stream(data->chunked.send(data.retain()))
					.thenAccept(v->chunked.finish())));
		
	}

	private Future<Void> sendMessages(ChunkedResponseContext chunked, int message, Integer count, boolean jsonResp) {
		return chunked.send(jsonResp?json.serializeAsString(Collections.singletonMap("message", "Hello "+message),CharsetUtil.UTF_8):"Hello "+message)
				.thenCombine(f->{
					if(message<count) {
						Promise<Void> promise = chunked.executor().newPromise();
						chunked.executor().schedule(()->sendMessages(chunked,message+1,count,jsonResp)
								.thenAccept(promise::setSuccess)
								.exceptionally(promise::setFailure), 100, TimeUnit.MILLISECONDS);
						return promise;
					} else {
						return chunked.executor().newSucceededFuture(null);
					}
				});
	}

}
