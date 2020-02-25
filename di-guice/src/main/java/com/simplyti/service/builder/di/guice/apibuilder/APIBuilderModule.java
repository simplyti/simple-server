package com.simplyti.service.builder.di.guice.apibuilder;

import java.util.Collection;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.server.http.api.ApiProvider;
import com.simplyti.server.http.api.builder.ApiBuilder;
import com.simplyti.server.http.api.builder.ApiBuilderImpl;
import com.simplyti.server.http.api.handler.ApiInvocationHandler;
import com.simplyti.server.http.api.handler.ApiRequestHandlerInit;
import com.simplyti.server.http.api.handler.ApiResponseEncoder;
import com.simplyti.server.http.api.health.HealthApi;
import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.operations.ApiOperationResolverImpl;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ApiOperationsImpl;
import com.simplyti.service.api.builder.di.InstanceProvider;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.sse.ServerSentEventEncoder;

public class APIBuilderModule extends AbstractModule {
	
	private final Collection<ApiProvider> apiProviders;
	private final Collection<Class<? extends ApiProvider>> apiClasses;
	
	
	public APIBuilderModule(Collection<ApiProvider> apiProviders,Collection<Class<? extends ApiProvider>> apiClasses) {
		this.apiProviders=apiProviders;
		this.apiClasses=apiClasses;
	}

	@Override
	public void configure() {
		Multibinder.newSetBinder(binder(), HandlerInit.class).addBinding().to(ApiRequestHandlerInit.class).in(Singleton.class);
		bind(ApiInvocationHandler.class).in(Singleton.class);
		bind(ApiResponseEncoder.class).in(Singleton.class);
		bind(ServerSentEventEncoder.class).in(Singleton.class);
		
		bind(ApiOperations.class).to(ApiOperationsImpl.class).in(Singleton.class);
		bind(ApiOperationResolver.class).to(ApiOperationResolverImpl.class).in(Singleton.class);
		
		bind(ApiBuilder.class).to(ApiBuilderImpl.class).in(Singleton.class);
		bind(InstanceProvider.class).to(GuiceInstanceProvider.class).in(Singleton.class);
		
		Multibinder<ApiProvider> apiBinder = Multibinder.newSetBinder(binder(), ApiProvider.class);
		apiBinder.addBinding().to(HealthApi.class).in(Singleton.class);
		
		apiProviders.forEach(provider->apiBinder.addBinding().toInstance(provider));
		apiClasses.forEach(apiClass->apiBinder.addBinding().to(apiClass).in(Singleton.class));
	}

}
