package com.simplyti.service.examples.api;

import com.simplyti.service.api.builder.ApiBuilder;

import com.simplyti.service.api.builder.ApiProvider;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class SSEApi implements ApiProvider{
	
	@Inject
	private EventLoopGroup eventLoopGroup;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/sse")
			.then(ctx->{
				ctx.serverSentEvent(sse->{
					Promise<Void> sendFuture = ctx.executor().newPromise();
					PromiseCombiner combiner = new PromiseCombiner(ctx.channel().eventLoop());
					combiner.add(sse.send("Hello!"));
					ctx.channel().eventLoop().schedule(()->{
						combiner.add(sse.send("Bye!"));
						combiner.finish(sendFuture);
					},200,TimeUnit.MILLISECONDS);
					sendFuture.addListener(f->ctx.channel().close());
				});
			});
		
		builder.when().get("/sse/disrrupt")
			.then(ctx->{
				ctx.serverSentEvent(sse->{
					eventLoopGroup.next().execute(()->sse.send("Hello!")
							.thenCombine(n->eventLoopGroup.next().schedule(()->sse.send("Bye!"), 200,TimeUnit.MILLISECONDS))
							.thenAccept(n->ctx.channel().close()));
				});
		});
	}

}
