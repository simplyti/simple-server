package com.simplyti.server.http.api.builder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableMap;
import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.server.http.api.context.AnyWithBodyApiContext;
import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;
import com.simplyti.server.http.api.context.RequestTypedApiContext;
import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;
import com.simplyti.server.http.api.context.fileupload.FileUploadAnyApiContext;
import com.simplyti.server.http.api.context.stream.StreamdRequestApiContext;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.operations.ApiOperationResolverImpl;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ApiOperationsImpl;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.handler.codec.http.HttpMethod;

@RunWith(Parameterized.class)
public class ApiBuilderTest {

	private static final int DEFAULT_MAX_BODY = 10000000;
	
	private final ApiOperationResolver resolver;
	private final ApiOperations operations;
	private final ApiBuilder builder;
	private final ApiProvider provider;
	
	private final HttpMethod method;
	private final String path;
	private final Class<? extends ApiContext> contextClass;
	private final int maxBody;
	private final boolean notFoundOnNull;
	private final Map<String,Object> meta;
	
	
	public ApiBuilderTest(ApiProvider provider, HttpMethod method, String uri, Class<? extends ApiContext> contextClass, 
			int maxBody, boolean notFoundOnNull, Map<String,Object> meta) {
		this.operations = new ApiOperationsImpl();
		this.builder = new ApiBuilderImpl(operations, null, null, null, null);
		this.provider=provider;
		this.resolver=new ApiOperationResolverImpl(operations, Collections.emptySet(), builder);
		this.method = method;
		this.path = uri;
		this.contextClass = contextClass;
		this.maxBody=maxBody;
		this.notFoundOnNull=notFoundOnNull;
		this.meta=meta;
   }

	@Before
	public void setup() {
		provider.build(builder);
	}

	@Parameters
	public static Collection<Object[]> builderTest() {
		return Arrays.asList(new Object[][] { 
			// Any type
			{ (ApiProvider) b -> b.when().get("/resource").then(ctx->ctx.send("OK")), HttpMethod.GET, "/resource", AnyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()},
			{ (ApiProvider) b -> b.when().post("/resource").then(ctx->ctx.send("OK")), HttpMethod.POST, "/resource", AnyWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().delete("/resource").then(ctx->ctx.send("OK")), HttpMethod.DELETE, "/resource", AnyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().put("/resource").then(ctx->ctx.send("OK")), HttpMethod.PUT, "/resource", AnyWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().patch("/resource").then(ctx->ctx.send("OK")), HttpMethod.PATCH, "/resource", AnyWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			
			// Typed
			{ (ApiProvider) b -> b.when().get("/resource").withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.GET, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().post("/resource").withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.POST, "/resource", ResponseTypedWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().delete("/resource").withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.DELETE, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().put("/resource").withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.PUT, "/resource", ResponseTypedWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.PATCH, "/resource", ResponseTypedWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			
			{ (ApiProvider) b -> b.when().post("/resource").withRequestBodyType(Integer.class).then(ctx->ctx.send(Integer.toString(ctx.body()))), HttpMethod.POST, "/resource", RequestTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withRequestBodyType(Integer.class).then(ctx->ctx.send(Integer.toString(ctx.body()))), HttpMethod.PUT, "/resource", RequestTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withRequestBodyType(Integer.class).then(ctx->ctx.send(Integer.toString(ctx.body()))), HttpMethod.PATCH, "/resource", RequestTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			
			{ (ApiProvider) b -> b.when().post("/resource").withRequestBodyType(Integer.class).withResponseBodyType(Integer.class).then(ctx->ctx.send(ctx.body())), HttpMethod.POST, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withRequestBodyType(Integer.class).withResponseBodyType(Integer.class).then(ctx->ctx.send(ctx.body())), HttpMethod.PUT, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withRequestBodyType(Integer.class).withResponseBodyType(Integer.class).then(ctx->ctx.send(ctx.body())), HttpMethod.PATCH, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().post("/resource").withResponseBodyType(Integer.class).withRequestBodyType(Integer.class).then(ctx->ctx.send(ctx.body())), HttpMethod.POST, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withResponseBodyType(Integer.class).withRequestBodyType(Integer.class).then(ctx->ctx.send(ctx.body())), HttpMethod.PUT, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withResponseBodyType(Integer.class).withRequestBodyType(Integer.class).then(ctx->ctx.send(ctx.body())), HttpMethod.PATCH, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			
			// Type Literals
			{ (ApiProvider) b -> b.when().get("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(1)), HttpMethod.GET, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().post("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(1)), HttpMethod.POST, "/resource", ResponseTypedWithBodyApiContext.class, DEFAULT_MAX_BODY , false, Collections.emptyMap() },
			{ (ApiProvider) b -> b.when().delete("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(1)), HttpMethod.DELETE, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(1)), HttpMethod.PUT, "/resource", ResponseTypedWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(1)), HttpMethod.PATCH, "/resource", ResponseTypedWithBodyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			
			{ (ApiProvider) b -> b.when().post("/resource").withRequestBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(Integer.toString(ctx.body()))), HttpMethod.POST, "/resource", RequestTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withRequestBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(Integer.toString(ctx.body()))), HttpMethod.PUT, "/resource", RequestTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withRequestBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(Integer.toString(ctx.body()))), HttpMethod.PATCH, "/resource", RequestTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			
			{ (ApiProvider) b -> b.when().post("/resource").withRequestBodyType(new TypeLiteral<Integer>() {}).withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(ctx.body())), HttpMethod.POST, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withRequestBodyType(new TypeLiteral<Integer>() {}).withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(ctx.body())), HttpMethod.PUT, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withRequestBodyType(new TypeLiteral<Integer>() {}).withResponseBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(ctx.body())), HttpMethod.PATCH, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().post("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).withRequestBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(ctx.body())), HttpMethod.POST, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().put("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).withRequestBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(ctx.body())), HttpMethod.PUT, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
			{ (ApiProvider) b -> b.when().patch("/resource").withResponseBodyType(new TypeLiteral<Integer>() {}).withRequestBodyType(new TypeLiteral<Integer>() {}).then(ctx->ctx.send(ctx.body())), HttpMethod.PATCH, "/resource", RequestResponseTypedApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()  },
		
			// Not found on null; max body modifiers
			{ (ApiProvider) b -> b.when().get("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().then(ctx->ctx.send("OK")), HttpMethod.GET, "/resource", AnyApiContext.class, DEFAULT_MAX_BODY, true, ImmutableMap.of("key1", "value1", "key2", "value2")},
			{ (ApiProvider) b -> b.when().post("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).then(ctx->ctx.send("OK")), HttpMethod.POST, "/resource", AnyWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().delete("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().then(ctx->ctx.send("OK")), HttpMethod.DELETE, "/resource", AnyApiContext.class, DEFAULT_MAX_BODY, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().put("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).then(ctx->ctx.send("OK")), HttpMethod.PUT, "/resource", AnyWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().patch("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).then(ctx->ctx.send("OK")), HttpMethod.PATCH, "/resource", AnyWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2") },

			{ (ApiProvider) b -> b.when().get("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.GET, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().post("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.POST, "/resource", ResponseTypedWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().delete("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.DELETE, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().put("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.PUT, "/resource", ResponseTypedWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2")  },
			{ (ApiProvider) b -> b.when().patch("/resource").withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).withResponseBodyType(Integer.class).then(ctx->ctx.send(1)), HttpMethod.PATCH, "/resource", ResponseTypedWithBodyApiContext.class,10, true, ImmutableMap.of("key1", "value1", "key2", "value2")  },
			{ (ApiProvider) b -> b.when().get("/resource").withResponseBodyType(Integer.class).withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().then(ctx->ctx.send(1)), HttpMethod.GET, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().post("/resource").withResponseBodyType(Integer.class).withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).then(ctx->ctx.send(1)), HttpMethod.POST, "/resource", ResponseTypedWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().delete("/resource").withResponseBodyType(Integer.class).withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().then(ctx->ctx.send(1)), HttpMethod.DELETE, "/resource", ResponseTypedApiContext.class, DEFAULT_MAX_BODY, true, ImmutableMap.of("key1", "value1", "key2", "value2") },
			{ (ApiProvider) b -> b.when().put("/resource").withResponseBodyType(Integer.class).withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).then(ctx->ctx.send(1)), HttpMethod.PUT, "/resource", ResponseTypedWithBodyApiContext.class, 10, true, ImmutableMap.of("key1", "value1", "key2", "value2")  },
			{ (ApiProvider) b -> b.when().patch("/resource").withResponseBodyType(Integer.class).withMeta("key1", "value1").withMeta("key2", "value2").withNotFoundOnNull().withMaximunBodyLength(10).then(ctx->ctx.send(1)), HttpMethod.PATCH, "/resource", ResponseTypedWithBodyApiContext.class,10, true, ImmutableMap.of("key1", "value1", "key2", "value2")  },
			
			// Streamed input
			{ (ApiProvider) b -> b.when().post("/resource").withStreamedInput().then(ctx->ctx.send("OK")), HttpMethod.POST, "/resource", StreamdRequestApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()},
			
			// File upload
			{ (ApiProvider) b -> b.when().post("/resource").asFileUpload().then(ctx->ctx.send("OK")), HttpMethod.POST, "/resource", FileUploadAnyApiContext.class, DEFAULT_MAX_BODY, false, Collections.emptyMap()},
						
		});
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testPrimeNumberChecker() {
		ApiMatchRequest matchRequest = this.resolver.resolveOperation(method, path);
		assertThat(matchRequest, notNullValue());
		
		ApiOperation operation = matchRequest.operation();
		assertThat(operation.maxBodyLength(),equalTo(maxBody));
		assertThat(operation.notFoundOnNull(),equalTo(notFoundOnNull));
		assertThat(operation.metadata(),equalTo(meta));
		meta.forEach((k,v)->assertThat(operation.meta(k),equalTo(v)));
		
		ApiContext ctx = mock(contextClass);
		if(ctx instanceof RequestTypedApiContext) {
			when(((RequestTypedApiContext) ctx).body()).thenReturn(1);
		}
		operation.handler().accept(ctx);
	}
	

}
