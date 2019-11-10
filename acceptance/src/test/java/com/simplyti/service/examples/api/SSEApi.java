package com.simplyti.service.examples.api;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.sse.SSEStream;

import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class SSEApi implements ApiProvider{

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/sse")
			.then(ctx->{
				Promise<Void> sendFuture = ctx.executor().newPromise();
				PromiseCombiner combiner = new PromiseCombiner(ctx.channel().eventLoop());
				SSEStream sse = ctx.sse();
				combiner.add(sse.send("Hello!"));
				ctx.channel().eventLoop().schedule(()->{
					combiner.add(sse.send("Bye!"));
					combiner.finish(sendFuture);
				},200,TimeUnit.MILLISECONDS);
				sendFuture.addListener(f->ctx.channel().close());
			});
	}

}
