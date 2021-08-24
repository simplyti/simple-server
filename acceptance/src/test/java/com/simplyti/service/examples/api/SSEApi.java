package com.simplyti.service.examples.api;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.simplyti.service.api.builder.ApiBuilder;

import com.simplyti.service.api.builder.ApiProvider;

import io.netty.channel.EventLoopGroup;

public class SSEApi implements ApiProvider{
	
	@Inject
	private EventLoopGroup eventLoopGroup;
	
	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/sse")
			.then(ctx->ctx.serverSentEvent(sse->sse.send("Hello")
					.thenCombine(f->ctx.executor().schedule(()->sse.send("Bye"), 200,TimeUnit.MILLISECONDS))
					.thenAccept(f->ctx.close())));
		
		builder.when().get("/sse/disrrupt")
			.then(ctx->{
				ctx.serverSentEvent(sse->eventLoopGroup.next().execute(()->sse.send("Hello!")
						.thenCombine(n->eventLoopGroup.next().schedule(()->sse.send("Bye!"), 200,TimeUnit.MILLISECONDS))
						.thenAccept(n->ctx.channel().close())));
		});
	}

}
