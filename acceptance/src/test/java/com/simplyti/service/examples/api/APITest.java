package com.simplyti.service.examples.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.JsoniterSpi;
import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.server.http.api.context.chunked.ChunkedResponseContext;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.exception.BadRequestException;
import com.simplyti.service.exception.UnauthorizedException;

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
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import sun.security.x509.X500Name;

@SuppressWarnings("restriction")
public class APITest implements ApiProvider {
	
	@Inject
	private EventLoopGroup eventLoopGroup;

	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/get")
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"GET!"));
		
		builder.when().get("/get/notfoundnull")
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"GET!"));
		
		builder.when().get("/get/dto")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("GET!")));
		
		builder.when().get("/get/notfoundnull/dto")
			.withNotFoundOnNull()
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("GET!")));
		
		builder.when().get("/get/dto/notfoundnull")
			.withResponseBodyType(APITestDTO.class)
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("GET!")));
		
		builder.when().delete("/delete")
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"DELETE!"));
		
		builder.when().delete("/delete/notfoundnull")
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"DELETE!"));
		
		builder.when().delete("/delete/dto")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("DELETE!")));
		
		builder.when().delete("/delete/notfoundnull/dto")
			.withNotFoundOnNull()
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("DELETE!")));
		
		builder.when().delete("/delete/dto/notfoundnull")
			.withResponseBodyType(APITestDTO.class)
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("DELETE!")));
		
		builder.when().post("/post")
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"POST!"));
		
		builder.when().post("/post/notfoundnull")
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"POST!"));
	
		builder.when().post("/post/dto")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("POST!")));
		
		builder.when().post("/post/dto/echo")
			.withRequestBodyType(APITestDTO.class)
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.body()));
		
		builder.when().post("/post/notfoundnull/dto")
			.withNotFoundOnNull()
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("POST!")));
		
		builder.when().post("/post/dto/notfoundnull")
			.withResponseBodyType(APITestDTO.class)
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("POST!")));
		
		builder.when().put("/put")
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"PUT!"));
		
		builder.when().put("/put/notfoundnull")
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"PUT!"));

		builder.when().put("/put/dto")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("PUT!")));
		
		builder.when().put("/put/dto/echo")
			.withRequestBodyType(APITestDTO.class)
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.body()));
		
		builder.when().put("/put/notfoundnull/dto")
			.withNotFoundOnNull()
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("PUT!")));
		
		builder.when().put("/put/dto/notfoundnull")
			.withResponseBodyType(APITestDTO.class)
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("PUT!")));
		
		builder.when().patch("/patch")
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"PATCH!"));
		
		builder.when().options("/options")
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"OPTIONS!"));
		
		builder.when().patch("/patch/notfoundnull")
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:"PATCH!"));

		builder.when().patch("/patch/dto")
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("PATCH!")));
		
		builder.when().patch("/patch/dto/echo")
			.withRequestBodyType(APITestDTO.class)
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.body()));
		
		builder.when().patch("/patch/notfoundnull/dto")
			.withNotFoundOnNull()
			.withResponseBodyType(APITestDTO.class)
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("PATCH!")));
		
		builder.when().patch("/patch/dto/notfoundnull")
			.withResponseBodyType(APITestDTO.class)
			.withNotFoundOnNull()
			.then(ctx->ctx.send(ctx.queryParaAsBoolean("null")?null:new APITestDTO("PATCH!")));
		
		
		builder.when().get("/hello")
			.then(ctx->ctx.send("Hello!"));
		
		builder.when().get("/hello/tlsname")
		.then(ctx->{
			try {
				X500Name principal = (X500Name) ctx.channel().pipeline().get(SslHandler.class).engine().getSession().getPeerCertificateChain()[0].getSubjectDN();
				ctx.send("Hello "+principal.getCommonName()+"!");
			} catch (IOException e) {
				ctx.failure(e);
			}
		});
		
		builder.when().get("/empty")
			.then(ctx->ctx.sendEmpty());
		
		builder.when().post("/echo")
			.then(ctx->ctx.send(ctx.body().copy()));
		
		builder.when().post("/header/echo")
			.then(ctx->{
				ByteBuf buff = ctx.channel().alloc().buffer();
				buff.writeBytes(ctx.body());
				if(ctx.request().headers().contains("x-req-filter")) {
					buff.writeCharSequence(" "+ctx.request().headers().get("x-req-filter"), CharsetUtil.UTF_8);
				}
				ctx.send(buff);
			});
		
		builder.when().post("/echo/request")
			.then(ctx->ctx.send(RequestDto.builder()
					.body(ctx.body().toString(CharsetUtil.UTF_8))
					.headers(toMap(ctx.request().headers()))
					.params(toMap(ctx.queryParams()))
					.build()));
		
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
		
		builder.when().get("/hello/close")
			.then(ctx->{
				Integer delay = ctx.queryParamAsInt("delay");
				ctx.send("Hello!").addListener(f->{
					if(delay == null || delay == 0) {
						ctx.close();
					} else {
						ctx.executor().schedule(ctx::close, delay, TimeUnit.MILLISECONDS);
					}
				});
			});
		
		builder.when().get("/remote")
			.then(ctx->ctx.send(((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().toString()));
		
		builder.when().post("/echo/delay")
			.then(ctx->eventLoopGroup.next().schedule(()->ctx.send(ctx.body().copy()), Long.parseLong(ctx.queryParam("millis")), TimeUnit.MILLISECONDS));
		
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
		
		builder.when().get("/future")
			.thenFuture(ctx->futureResponse(ctx));
		
		builder.when().post("/typed/request/response/future")
			.withRequestBodyType(APITestDTO.class)
			.withResponseBodyType(APITestDTO.class)
			.thenFuture(ctx->futureResponse(ctx));
		
		builder.when().get("/typed/response/sync")
			.withResponseBodyType(APITestDTO.class)
			.thenFuture(ctx->ctx.sync(()->new APITestDTO("Hello from thread "+Thread.currentThread().getName())));
		
		builder.when().get("/void/sync")
			.thenFuture(ctx->ctx.sync(this::syncTask));
		
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
				try{
					int status = Integer.parseInt(ctx.pathParam("status"));
					FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.valueOf(status),Unpooled.EMPTY_BUFFER);
					response.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
					ctx.send(response);
				}catch (NumberFormatException  e) {
					ctx.failure(new BadRequestException());
				}
			});
		
		builder.when().post("/responsecode/{status}")
			.then(ctx->{
				FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.valueOf(Integer.parseInt(ctx.pathParam("status"))), ctx.body().copy());
				response.headers().set(HttpHeaderNames.CONTENT_LENGTH,ctx.body().readableBytes());
				ctx.send(response);
			});
		
		builder.when().get("/base64/{data}")
			.then(ctx-> ctx.send(new String(Base64.getDecoder().decode(ctx.pathParam("data")))));
		
		builder.when().get("/user/{id}")
			.withMeta("serviceId", "GetUser")
			.then(ctx->ctx.send("Hello user "+ctx.pathParam("id")));
		
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
		
		builder.when().get("/basic-auth/{user}/{pass}")
			.then(ctx->basicAuth("Hello", ctx));
		
		builder.when().post("/basic-auth/{user}/{pass}")
			.then(ctx->basicAuth(ctx.body().toString(CharsetUtil.UTF_8), ctx));
		
		builder.when().get("/bearer-auth/{user}")
			.then(ctx->bearerAuth("Hello", ctx));
	
		builder.when().post("/bearer-auth/{user}")
			.then(ctx->bearerAuth(ctx.body().toString(CharsetUtil.UTF_8), ctx));
		
		JsoniterSpi.registerTypeEncoder(SerializedErrorDTO.class, (obj,stream)->{throw new RuntimeException("No serializable");});
		builder.when().get("/json/serialize/error")
			.withResponseBodyType(SerializedErrorDTO.class)
			.then(ctx->ctx.send(new SerializedErrorDTO()));
		
		builder.when().get("/stream/json")
			.then(ctx->{
				ctx.sendChunked(stream->{
					int total = MoreObjects.firstNonNull(ctx.queryParamAsInt("count"), 5);
					AtomicInteger count = new AtomicInteger(total);
					sendJson(ctx.executor(), stream, count, total);
				});
			});
		
		builder.when().get("/stream/text")
			.then(ctx->{
				ctx.sendChunked(stream->{
					int total = MoreObjects.firstNonNull(ctx.queryParamAsInt("count"), 5);
					AtomicInteger count = new AtomicInteger(total);
					sendText(ctx.executor(), stream, count, total);
				});
			});
		
		builder.when().post("/echo/chunked")
			.withStreamedInput()
			.then(ctx->
				ctx.sendChunked(chunked->
					ctx.stream(data->chunked.send(data.retain()))
					.thenAccept(v->chunked.finish())));
		
		builder.usingJaxRSContract(JaxRSAPITest.class);
	}

	private void sendJson(EventExecutor executor, ChunkedResponseContext stream, AtomicInteger count, int total) {
		executor.schedule(()->{
			if(count.get()>0) {
				stream.send(JsonStream.serialize(Collections.singletonMap("count", "Message "+(total - count.decrementAndGet()))))
					.addListener(f->sendJson(executor, stream, count, total));
			} else {
				stream.finish();
			}
		}, 50, TimeUnit.MILLISECONDS);
	}
	
	private void sendText(EventExecutor executor, ChunkedResponseContext stream, AtomicInteger count, int total) {
		executor.schedule(()->{
			if(count.get()>0) {
				stream.send("Message "+(total - count.decrementAndGet()))
					.addListener(f->sendText(executor, stream, count, total));
			} else {
				stream.finish();
			}
		}, 50, TimeUnit.MILLISECONDS);
		
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

	private void basicAuth(String prefix, AnyApiContext ctx) {
		String auth = ctx.request().headers().get(HttpHeaderNames.AUTHORIZATION);
		String expectedAuth = "Basic " + Base64.getEncoder().encodeToString((ctx.pathParam("user")+":"+ctx.pathParam("pass")).getBytes());
		if(auth !=null && auth.equals(expectedAuth)) {
			ctx.send(prefix+" "+ctx.pathParam("user"));
		} else {
			ctx.failure(new UnauthorizedException());
		}
	}

	private Map<String,String> toMap(HttpHeaders headers) {
		return headers.entries().stream()
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	private Map<String,String> toMap(Map<String,List<String>> params) {
		return params.entrySet().stream()
			.collect(Collectors.toMap(Entry::getKey, e->e.getValue().get(0)));
	}

	private Future<APITestDTO> futureResponse(AnyApiContext ctx) {
		Promise<APITestDTO> promise = ctx.executor().newPromise();
		ctx.executor().schedule(()->promise.setSuccess(new APITestDTO("Hello future!")), 10, TimeUnit.MILLISECONDS);
		return promise;
	}

	private Future<APITestDTO> futureResponse(RequestResponseTypedApiContext<APITestDTO, APITestDTO> ctx) {
		Promise<APITestDTO> promise = ctx.executor().newPromise();
		ctx.executor().schedule(()->promise.setSuccess(ctx.body()), 10, TimeUnit.MILLISECONDS);
		return promise;
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
	
	private void syncTask() {
		
	}

}
