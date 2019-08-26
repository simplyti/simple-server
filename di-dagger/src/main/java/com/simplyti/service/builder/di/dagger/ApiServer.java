package com.simplyti.service.builder.di.dagger;

import java.util.Set;

import javax.inject.Singleton;

import com.simplyti.service.api.ApiResolver;
import com.simplyti.service.api.DefaultApiResolver;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.builder.di.InstanceProvider;
import com.simplyti.service.api.filter.OperationInboundFilter;
import com.simplyti.service.api.health.HealthApi;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.inits.ApiRequestHandlerInit;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sse.ServerSentEventEncoder;
import com.simplyti.service.sync.SyncTaskSubmitter;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

@Module
public class ApiServer {

	@Provides
	@IntoSet
	public HandlerInit apiRequestHandlerInit(ExceptionHandler exceptionHandler, SyncTaskSubmitter syncTaskSubmitter,
			ApiResolver apiResolver, ApiResponseEncoder apiResponseEncoder, ApiInvocationHandler apiInvocationHandler,
			ServerHeadersHandler serverHeadersHandler) {
		return new ApiRequestHandlerInit(apiResolver, apiResponseEncoder, apiInvocationHandler, exceptionHandler,syncTaskSubmitter,serverHeadersHandler);
	}
	
	@Provides
	@Singleton
	public ApiInvocationHandler apiInvocationHandler(Set<OperationInboundFilter> operationInboundFilters,
			ExceptionHandler exceptionHandler, ServerSentEventEncoder serverSentEventEncoder,
			SyncTaskSubmitter syncTaskSubmitter, Json json) {
		return new ApiInvocationHandler(operationInboundFilters, exceptionHandler, serverSentEventEncoder,syncTaskSubmitter, json);
	}
	
	@Provides
	@Singleton
	public ApiResolver apiResolver(ApiBuilder apiBuilder, Set<ApiProvider> providers) {
		return new DefaultApiResolver(providers, apiBuilder);
	}
	
	@Provides
	@Singleton
	public ApiResponseEncoder apiResponseEncoder(Json json) {
		return new ApiResponseEncoder(json);
	}
	
	@Provides
	@IntoSet
	public ApiProvider health() {
		return new HealthApi();
	}
	
	@Provides
	@Singleton
	public ApiBuilder apiBuilder(InstanceProvider instanceProvider, SyncTaskSubmitter syncTaskSubmitter) {
		return new ApiBuilder(instanceProvider, syncTaskSubmitter);
	}
	
	@Provides
	@Singleton
	public ServerSentEventEncoder serverSentEventEncoder() {
		return new ServerSentEventEncoder();
	}
	
}
