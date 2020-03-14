package com.simplyti.service.examples.api;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.jsoniter.spi.JsoniterSpi;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class APITest implements ApiProvider {
	
	@Inject
	private EventLoopGroup eventLoopGroup;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/hello")
			.then(ctx->ctx.send("Hello!"));
		
		builder.when().get("/empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().post("/echo")
			.then(ctx->ctx.send(ctx.body().copy()));
		
		builder.when().post("/echo/{id}")
			.then(ctx->ctx.send(ctx.body().copy()));
		
		builder.when().post("/echo/buffered")
		.then(ctx->{
			ByteBuf bodyCopy = ctx.body().retain();
			ctx.send(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, new DefaultHttpHeaders()
					.set(HttpHeaderNames.CONTENT_LENGTH,ctx.body().readableBytes())));
			while(bodyCopy.isReadable()) {
				ByteBuf content = bodyCopy.readSlice(Math.min(1024, bodyCopy.readableBytes()));
				if(bodyCopy.isReadable()) {
					ctx.send(new DefaultHttpContent(content.retain()));
				}else {
					ctx.send(new DefaultLastHttpContent(content.retain()));
				}
			}
			bodyCopy.release();
			
		});
		
		builder.when().get("/close")
			.then(ctx->ctx.close());
		
		builder.when().get("/remote")
			.then(ctx->ctx.send(((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().toString()));
		
		builder.when().post("/echo/delay")
			.then(ctx->eventLoopGroup.next().schedule(()->ctx.send(ctx.body().retain()), Long.parseLong(ctx.queryParam("millis")), TimeUnit.MILLISECONDS));
		
		builder.when().get("/throwexception")
			.then(ctx->throwRuntimeException(ctx.queryParam("message")));
		
		builder.when().get("/failure")
			.then(ctx->ctx.failure(new RuntimeException(ctx.queryParam("message"))));
		
		builder.when().get("/failure/delay")
			.then(ctx->ctx.executor().schedule(()->ctx.failure(new RuntimeException(ctx.queryParam("message"))), Long.parseLong(ctx.queryParam("millis")), TimeUnit.MILLISECONDS));
		
		builder.when().get("/pathparam/{name}")
			.then(ctx->ctx.send("Hello "+ctx.pathParam("name")));
		
		builder.when().get("/pathparam/unexisting")
			.then(ctx->ctx.send(nullableToString(ctx.pathParam("name"))));
		
		builder.when().get("/queryparam")
			.then(ctx->ctx.send("Hello "+nullableToString(ctx.queryParam("name"))));
		
		builder.when().get("/queryparams")
			.then(ctx->ctx.send(Joiner.on('m').join(ctx.queryParams("name"))));
		
		builder.when().get("/queryparams/all")
		.then(ctx->ctx.send(ctx.queryParams().toString()));
		
		builder.when().get("/uri")
			.then(ctx->ctx.send("Hello "+ctx.request().uri()));
		
		builder.when().get("/header/{key}")
			.then(ctx->ctx.send("Hello "+ctx.request().headers().get(ctx.pathParam("key"))));
		
		builder.when().post("/typed/request")
			.withRequestBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.body().getMessage()));
		
		builder.when().post("/typed/request/void")
			.withRequestBodyType(Void.class)
			.then(ctx->ctx.send(nullableToString(ctx.body())));
		
		builder.when().post("/typed/request/tostring")
			.withRequestBodyType(APITestDTO.class)
			.then(ctx->ctx.send(nullableToString(ctx.body())));
		
		builder.when().get("/typed/response")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(new APITestDTO("Typed response")));
		
		builder.when().get("/typed/response/future")
			.withResponseBodyType(APITestDTO.class)
			.thenFuture(ctx->futureResponse(ctx));
		
		builder.when().get("/typed/response/sync")
			.withResponseBodyType(APITestDTO.class)
			.thenFuture(ctx->ctx.sync(()->new APITestDTO("Hello from thread "+Thread.currentThread().getName())));
		
		builder.when().get("/hello/json")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(new APITestDTO(ctx.queryParam("name"))));
		
		builder.when().get("/hello/{id}")
			.then(ctx->ctx.send("Hello "+ctx.pathParam("id")+"!"));
		
		builder.when().post("/typed/request/response")
			.withRequestBodyType(APITestDTO.class)
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(new APITestDTO(ctx.body().getMessage())));
		
		builder.when().post("/typed/response/request")
			.withResponseBodyType(APITestDTO.class)
			.withRequestBodyType(APITestDTO.class)
			.then(ctx->ctx.send(new APITestDTO(ctx.body().getMessage())));
		
		builder.when().get("/wildcard/{whatever:.*}")
			.then(ctx->ctx.send(ctx.pathParam("whatever")));
		
		builder.when().get("/responsecode/{status}")
		.then(ctx->{
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.valueOf(Integer.parseInt(ctx.pathParam("status"))),Unpooled.EMPTY_BUFFER);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
			ctx.send(response);
		});
		
		builder.when().get("/user/{id}")
			.withMeta("serviceId", "GetUser")
			.then(ctx->ctx.send("Hello user "+ctx.pathParam("id")));
		
		builder.when().delete("/delete")
			.then(ctx->ctx.send("Bye!"));
		
		builder.when().get("/error/after/send")
			.then(ctx->{
				ctx.send("I Will send throw an error!");
				throw new RuntimeException("This is a test error");
			});
	
		builder.when().get("/resources")
			.then(ctx->ctx.send("This is the resource list"));
		
		builder.when().get("/resources/{id}")
			.then(ctx->ctx.send("This is the resource "+ctx.pathParam("id")));
		
		builder.when().get("/anything")
			.then(ctx->ctx.send("This is a prioritized response"));
		
		JsoniterSpi.registerTypeEncoder(SerializedErrorDTO.class, (obj,stream)->{throw new RuntimeException("No serializable");});
		builder.when().get("/json/serialize/error")
			.withResponseBodyType(SerializedErrorDTO.class)
			.then(ctx->ctx.send(new SerializedErrorDTO()));
		
		builder.usingJaxRSContract(JaxRSAPITest.class);
	}

	private Future<APITestDTO> futureResponse(ResponseTypedApiContext<APITestDTO> ctx) {
		Promise<APITestDTO> promise = ctx.executor().newPromise();
		ctx.executor().schedule(()->promise.setSuccess(new APITestDTO("Hello future!")), 10, TimeUnit.MILLISECONDS);
		return promise;
	}

	private void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}

	private String nullableToString(Object obj) {
		return obj==null?"null":obj.toString();
	}

}
