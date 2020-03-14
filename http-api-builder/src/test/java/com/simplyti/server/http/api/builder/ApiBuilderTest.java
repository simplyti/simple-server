package com.simplyti.server.http.api.builder;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.server.http.api.context.AnyWithBodyApiContext;
import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;
import com.simplyti.server.http.api.context.RequestTypedApiContext;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.operations.ApiOperationResolverImpl;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ApiOperationsImpl;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.serializer.json.Json;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

@RunWith(MockitoJUnitRunner.class)
public class ApiBuilderTest {
	
	private ApiOperations operations;
	private ApiBuilder builder;
	private ApiOperationResolver resolver;
	private Json json;
	private QueryStringDecoder queryDecoder;
	
	@Before
	public void setup() {
		this.json = mock(Json.class);
		this.queryDecoder = mock(QueryStringDecoder.class);
		this.operations = new ApiOperationsImpl();
		this.builder = new ApiBuilderImpl(operations,null,null,null,json);
		this.resolver=new ApiOperationResolverImpl(operations, Collections.emptySet(), builder);
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildRawGetApi() {
		builder.when().get("/")
			.then(ctx->ctx.writeAndFlush("Hello!"));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.GET,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		AnyApiContext ctx = mock(AnyApiContext.class);
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("Hello!");
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildResponseTypeGetApi() {
		builder.when().delete("/")
			.withResponseType(String.class)
			.then(ctx->ctx.writeAndFlush("Hello!"));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.DELETE,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		ResponseTypedApiContext<String> ctx = mock(ResponseTypedApiContext.class);
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("Hello!");
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildRawPostApi() {
		builder.when().post("/")
			.then(ctx->ctx.writeAndFlush("Hello!"));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.POST,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		AnyWithBodyApiContext ctx = mock(AnyWithBodyApiContext.class);
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("Hello!");
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildRequestTypePostApi() {
		builder.when().put("/")
			.withRequestType(String.class)
			.then(ctx->ctx.writeAndFlush(ctx.body()));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.PUT,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		RequestTypedApiContext<String> ctx = mock(RequestTypedApiContext.class);
		when(ctx.body()).thenReturn("Hello Body!");
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("Hello Body!");
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildRequestResponseTypePostApi() {
		builder.when().patch("/")
			.withRequestType(Integer.class)
			.withResponseType(String.class)
			.then(ctx->ctx.writeAndFlush(ctx.body().toString()));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.PATCH,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		RequestResponseTypedApiContext<Integer,String> ctx = mock(RequestResponseTypedApiContext.class);
		when(ctx.body()).thenReturn(1);
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("1");
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildResponseRequestTypePostApi() {
		builder.when().post("/")
			.withResponseType(Integer.class)
			.withRequestType(String.class)
			.then(ctx->ctx.writeAndFlush(ctx.body().hashCode()));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.POST,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		RequestResponseTypedApiContext<String,Integer> ctx = mock(RequestResponseTypedApiContext.class);
		when(ctx.body()).thenReturn("Hello!");
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush(-2137068113);
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildResponseTypePostApi() {
		builder.when().post("/")
			.withResponseType(Integer.class)
			.then(ctx->ctx.writeAndFlush(ctx.body().readableBytes()));
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.POST,"/",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		ResponseTypedWithBodyApiContext<Integer> ctx = mock(ResponseTypedWithBodyApiContext.class);
		when(ctx.body()).thenReturn(Unpooled.copiedBuffer("Hello!", CharsetUtil.UTF_8));
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush(6);
	}
	
	@Test
	public void noApiMatch() {
		builder.when().get("/").then(ApiContext::close);
		
		assertThat(resolver.resolveOperation(HttpMethod.POST,"/",queryDecoder),nullValue());
		assertThat(resolver.resolveOperation(HttpMethod.GET,"/other",queryDecoder),nullValue());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priorizedApiMatch() {
		builder.when().get("/users/{id}").then(ctx->ctx.writeAndFlush("Hello User"));
		builder.when().get("/users/me").then(ctx->ctx.writeAndFlush("Hello You"));
		this.operations.sort();
		
		ApiMatchRequest matchRequest = resolver.resolveOperation(HttpMethod.GET,"/users/1",queryDecoder);
		assertThat(matchRequest,notNullValue());
		ApiOperation operation = matchRequest.operation();
		
		AnyApiContext ctx = mock(AnyApiContext.class);
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("Hello User");
		
		matchRequest = resolver.resolveOperation(HttpMethod.GET,"/users/me",queryDecoder);
		assertThat(operation,notNullValue());
		operation = matchRequest.operation();
		
		ctx = mock(AnyApiContext.class);
		operation.handler().accept(ctx);
		verify(ctx,times(1)).writeAndFlush("Hello You");
	}

}
