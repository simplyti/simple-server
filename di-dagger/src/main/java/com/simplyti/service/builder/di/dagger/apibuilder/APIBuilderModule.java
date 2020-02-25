package com.simplyti.service.builder.di.dagger.apibuilder;

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;
import com.simplyti.server.http.api.builder.ApiBuilderImpl;
import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.handler.ApiInvocationHandler;
import com.simplyti.server.http.api.handler.ApiRequestHandlerInit;
import com.simplyti.server.http.api.handler.ApiResponseEncoder;
import com.simplyti.server.http.api.health.HealthApi;
import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.operations.ApiOperationResolverImpl;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ApiOperationsImpl;
import com.simplyti.service.api.builder.di.InstanceProvider;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.sync.SyncTaskSubmitter;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module(includes= {APIBuilderOptionals.class})
public class APIBuilderModule {

	@Provides
	@IntoSet
	public HandlerInit apiRequestHandlerInit(ApiOperationResolver apiResolver,
			ApiResponseEncoder apiResponseEncoder,
			ApiInvocationHandler apiInvocationHandler, ExceptionHandler exceptionHandler,
			SyncTaskSubmitter syncTaskSubmitter, ServerHeadersHandler serverHeadersHandler) {
		return new ApiRequestHandlerInit(apiResolver, apiResponseEncoder, apiInvocationHandler, exceptionHandler, syncTaskSubmitter, serverHeadersHandler);
	}
	
	@Provides
	@Singleton
	public ApiInvocationHandler apiInvocationHandler(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, Set<OperationInboundFilter> operationInboundFilters) {
		return new ApiInvocationHandler(syncTaskSubmitter, exceptionHandler, operationInboundFilters);
	}
	
	@Provides
	@Singleton
	public ApiOperations apiOperations() {
		return new ApiOperationsImpl();
	}

	
	@Provides
	@Singleton
	public ApiOperationResolver apiResolver(ApiOperations operations, Set<ApiProvider> apis,ApiBuilder builder) {
		return new ApiOperationResolverImpl(operations,apis,builder);
	}
	
	@Provides
	@Singleton
	public ApiResponseEncoder apiResponseEncoder(Json json) {
		return new ApiResponseEncoder(json);
	}
	
	@Provides
	@Singleton
	public ServerSentEventEncoder serverSentEventEncoder() {
		return new ServerSentEventEncoder();
	}
	
	@Provides
	@Singleton
	public ApiBuilder apiBuilder(ApiOperations apiOperations, Json json) {
		return new ApiBuilderImpl(apiOperations, json);
	}
	
	@Provides
	@Singleton
	public InstanceProvider instanceProvider(Map<Class<?>,Object> instances) {
		return new DaggerInstanceProvider(instances);
	}
	
	@Provides
	@IntoSet
	public ApiProvider health() {
		return new HealthApi();
	}
	
}
