package com.simplyti.service.examples.api;

import java.util.concurrent.TimeUnit;

import com.simplyti.server.http.api.context.sse.ServerSentEventApiContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.Promise;

public class APISSEExample implements ApiProvider{
	
	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/sse")
			.then(ctx->ctx.serverSentEvent(sse->sendMessages(sse, 1, ctx.queryParamAsInt("count"))
					.thenAccept(f->ctx.close())));
		
	}

	private Future<Void> sendMessages(ServerSentEventApiContext sse, int message, int count) {
		return sse.send("Hello "+message)
			.thenCombine(f->{
				if(message<count) {
					Promise<Void> promise = sse.executor().newPromise();
					sse.executor().schedule(()->sendMessages(sse,message+1,count)
							.thenAccept(promise::setSuccess)
							.exceptionally(promise::setFailure), 100, TimeUnit.MILLISECONDS);
					return promise;
				} else {
					return sse.executor().newSucceededFuture(null);
				}
			});
	}

}
