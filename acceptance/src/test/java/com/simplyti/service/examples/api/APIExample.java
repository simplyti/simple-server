package com.simplyti.service.examples.api;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class APIExample implements ApiProvider {
	
	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/get")
			.then(ctx->ctx.send("Hello GET!"));
		
		builder.when().get("/get/future")
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), "Hello GET!"));
		
		builder.when().get("/get/response-type")
			.withResponseBodyType(APIExampleResponse.class)
			.then(ctx->ctx.send(APIExampleResponse.builder().message("Hello GET!").build()));
		
		builder.when().get("/get/response-type/future")
			.withResponseBodyType(APIExampleResponse.class)
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), APIExampleResponse.builder().message("Hello GET!").build()));
		
		builder.when().delete("/delete")
			.then(ctx->ctx.send("Hello DELETE!"));
		
		builder.when().delete("/delete/future")
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), "Hello DELETE!"));
		
		builder.when().delete("/delete/response-type")
			.withResponseBodyType(APIExampleResponse.class)
			.then(ctx->ctx.send(APIExampleResponse.builder().message("Hello DELETE!").build()));
	
		builder.when().delete("/delete/response-type/future")
			.withResponseBodyType(APIExampleResponse.class)
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), APIExampleResponse.builder().message("Hello DELETE!").build()));
		
		builder.when().post("/post")
			.then(ctx->ctx.send("Hello POST!"));
		
		builder.when().post("/post/future")
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), "Hello POST!"));
		
		builder.when().post("/post/response-type")
			.withResponseBodyType(APIExampleResponse.class)
			.then(ctx->ctx.send(APIExampleResponse.builder().message("Hello POST!").build()));
	
		builder.when().post("/post/response-type/future")
			.withResponseBodyType(APIExampleResponse.class)
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), APIExampleResponse.builder().message("Hello POST!").build()));
		
		builder.when().put("/put")
			.then(ctx->ctx.send("Hello PUT!"));
		
		builder.when().put("/put/future")
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), "Hello PUT!"));
		
		builder.when().put("/put/response-type")
			.withResponseBodyType(APIExampleResponse.class)
			.then(ctx->ctx.send(APIExampleResponse.builder().message("Hello PUT!").build()));

		builder.when().put("/put/response-type/future")
			.withResponseBodyType(APIExampleResponse.class)
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), APIExampleResponse.builder().message("Hello PUT!").build()));
		
		builder.when().patch("/patch")
			.then(ctx->ctx.send("Hello PATCH!"));
		
		builder.when().patch("/patch/future")
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), "Hello PATCH!"));
		
		builder.when().patch("/patch/response-type")
			.withResponseBodyType(APIExampleResponse.class)
			.then(ctx->ctx.send(APIExampleResponse.builder().message("Hello PATCH!").build()));

		builder.when().patch("/patch/response-type/future")
			.withResponseBodyType(APIExampleResponse.class)
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), APIExampleResponse.builder().message("Hello PATCH!").build()));
		
		builder.when().options("/options")
			.then(ctx->ctx.send("Hello OPTIONS!"));
		
		builder.when().options("/options/future")
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), "Hello OPTIONS!"));
		
		builder.when().options("/options/response-type")
			.withResponseBodyType(APIExampleResponse.class)
			.then(ctx->ctx.send(APIExampleResponse.builder().message("Hello OPTIONS!").build()));

		builder.when().options("/options/response-type/future")
			.withResponseBodyType(APIExampleResponse.class)
			.thenFuture(ctx->future(ctx, ctx.queryParamAsInt("delay"), APIExampleResponse.builder().message("Hello OPTIONS!").build()));
		
		builder.when().get("/get-numeric-id/{id:\\d+}")
			.then(ctx->ctx.send("Hello GET "+ctx.pathParam("id")+"!"));
		
		builder.when().get("/get-id/{id}")
			.then(ctx->ctx.send("Hello GET "+ctx.pathParam("id")+"!"));
		
		builder.when().get("/get-any/{id:.*}")
			.then(ctx->ctx.send("Hello GET "+ctx.pathParam("id")+"!"));
		
		builder.when().get("/get-empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().get("/get-null")
			.then(ctx->ctx.send((Object) null));
		
		builder.when().get("/get-null-notfound")
			.withNotFoundOnNull()
			.then(ctx->ctx.send((Object) null));
			
		builder.when().delete("/delete-numeric-id/{id:\\d+}")
			.then(ctx->ctx.send("Hello DELETE "+ctx.pathParam("id")+"!"));
		
		builder.when().delete("/delete-id/{id}")
			.then(ctx->ctx.send("Hello DELETE "+ctx.pathParam("id")+"!"));
		
		builder.when().delete("/delete-any/{id:.*}")
			.then(ctx->ctx.send("Hello DELETE "+ctx.pathParam("id")+"!"));
		
		builder.when().delete("/delete-empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().delete("/delete-null")
			.then(ctx->ctx.send((Object) null));
		
		builder.when().delete("/delete-null-notfound")
			.withNotFoundOnNull()
			.then(ctx->ctx.send((Object) null));
		
		builder.when().post("/post-numeric-id/{id:\\d+}")
			.then(ctx->ctx.send("Hello POST "+ctx.pathParam("id")+"!"));
		
		builder.when().post("/post-id/{id}")
			.then(ctx->ctx.send("Hello POST "+ctx.pathParam("id")+"!"));
		
		builder.when().post("/post-any/{id:.*}")
			.then(ctx->ctx.send("Hello POST "+ctx.pathParam("id")+"!"));
		
		builder.when().post("/post-empty")
			.then(ctx->ctx.send((Object) null));
		
		builder.when().post("/post-null")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().post("/post-null-notfound")
			.withNotFoundOnNull()
			.then(ctx->ctx.send((Object) null));
		
		builder.when().put("/put-numeric-id/{id:\\d+}")
			.then(ctx->ctx.send("Hello PUT "+ctx.pathParam("id")+"!"));
		
		builder.when().put("/put-id/{id}")
			.then(ctx->ctx.send("Hello PUT "+ctx.pathParam("id")+"!"));
		
		builder.when().put("/put-any/{id:.*}")
			.then(ctx->ctx.send("Hello PUT "+ctx.pathParam("id")+"!"));
		
		builder.when().put("/put-empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().put("/put-null")
			.then(ctx->ctx.send((Object) null));
		
		builder.when().put("/put-null-notfound")
			.withNotFoundOnNull()
			.then(ctx->ctx.send((Object) null));
		
		builder.when().patch("/patch-numeric-id/{id:\\d+}")
			.then(ctx->ctx.send("Hello PATCH "+ctx.pathParam("id")+"!"));
		
		builder.when().patch("/patch-id/{id}")
			.then(ctx->ctx.send("Hello PATCH "+ctx.pathParam("id")+"!"));
		
		builder.when().patch("/patch-any/{id:.*}")
			.then(ctx->ctx.send("Hello PATCH "+ctx.pathParam("id")+"!"));
		
		builder.when().patch("/patch-empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().patch("/patch-null")
			.then(ctx->ctx.send((Object) null));
		
		builder.when().patch("/patch-null-notfound")
			.withNotFoundOnNull()
			.then(ctx->ctx.send((Object) null));
		
		builder.when().options("/options-numeric-id/{id:\\d+}")
			.then(ctx->ctx.send("Hello OPTIONS "+ctx.pathParam("id")+"!"));
		
		builder.when().options("/options-id/{id}")
			.then(ctx->ctx.send("Hello OPTIONS "+ctx.pathParam("id")+"!"));
		
		builder.when().options("/options-any/{id:.*}")
			.then(ctx->ctx.send("Hello OPTIONS "+ctx.pathParam("id")+"!"));
		
		builder.when().options("/options-empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().options("/options-null")
			.then(ctx->ctx.send((Object) null));
		
		builder.when().options("/options-null-notfound")
			.withNotFoundOnNull()
			.then(ctx->ctx.send((Object) null));
		
		builder.when().post("/post/echo")
			.then(ctx->withDelay(ctx, ctx.queryParamAsInt("delay"),()->ctx.send(ctx.body().retain())));
		
		builder.when().put("/put/echo")
			.then(ctx->withDelay(ctx, ctx.queryParamAsInt("delay"),()->ctx.send(ctx.body().retain())));
		
		builder.when().patch("/patch/echo")
			.then(ctx->withDelay(ctx, ctx.queryParamAsInt("delay"),()->ctx.send(ctx.body().retain())));
		
		builder.when().post("/echo")
			.then(ctx->withDelay(ctx, ctx.queryParamAsInt("delay"),()->ctx.send(ctx.body().retain())));
		
		builder.when().get("/get/query-to-json")
			.then(ctx->ctx.send(ctx.queryParamNames().stream()
					.map(name->Maps.immutableEntry(name, ctx.queryParam(name)))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue))));
		
		builder.when().get("/get/headers-to-json")
			.then(ctx->ctx.send(ctx.request().headers().entries().stream()
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue))));
		
		builder.when().post("/post/headers-to-json")
			.then(ctx->ctx.send(Streams.concat(
					Collections.singletonList(Maps.immutableEntry("body", ctx.body().toString(CharsetUtil.UTF_8))).stream(),
					ctx.request().headers().entries().stream())
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue))));
		
		builder.when().get("/status/{code}")
			.then(ctx->ctx.send(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(ctx.pathParamAsInt("code")))));
		
		builder.when().post("/echo/status/{code}")
			.then(ctx->ctx.send(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(ctx.pathParamAsInt("code")), ctx.body().retain())));
		
		builder.when().get("/close")
			.then(ApiContext::close);
		
		builder.when().get("/get-and-close")
			.then(ctx->ctx.send("Hello GET!").addListener(f->withDelay(ctx, ctx.queryParamAsInt("delay"), ctx::close)));
		
	}

	private void withDelay(ApiContext ctx, Integer delay, Runnable command) {
		if(delay != null) {
			ctx.executor().schedule(command, delay, TimeUnit.MILLISECONDS);
		} else {
			command.run();
		}
	}
	
	private <T> Future<T> future(ApiContext ctx, Integer delay, T value) {
		Promise<T> promise = ctx.executor().newPromise();
		withDelay(ctx, 100, ()->promise.setSuccess(value));
		return promise;
	}

}
