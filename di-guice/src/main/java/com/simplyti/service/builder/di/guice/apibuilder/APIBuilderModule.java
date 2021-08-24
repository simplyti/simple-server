package com.simplyti.service.builder.di.guice.apibuilder;

import java.util.Collection;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.server.http.api.builder.ApiBuilderImpl;
import com.simplyti.server.http.api.handler.ApiResponseEncoder;
import com.simplyti.server.http.api.handler.init.ApiHandlerInit;
import com.simplyti.server.http.api.health.HealthApi;
import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.operations.ApiOperationResolverImpl;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.ApiOperationsImpl;
import com.simplyti.server.http.api.sse.ServerSentEventEncoder;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.matcher.di.InstanceProvider;

public class APIBuilderModule extends AbstractModule {
	
	private final Collection<Class<? extends ApiProvider>> apiClasses;
	
	public APIBuilderModule(Collection<Class<? extends ApiProvider>> apiClasses) {
		this.apiClasses=apiClasses;
	}

	@Override
	public void configure() {
		Multibinder.newSetBinder(binder(), ServiceHadlerInit.class).addBinding().to(ApiHandlerInit.class);
		
		bind(ApiResponseEncoder.class).in(Singleton.class);
		bind(ServerSentEventEncoder.class).in(Singleton.class);
		
		bind(ApiOperations.class).to(ApiOperationsImpl.class).in(Singleton.class);
		bind(ApiOperationResolver.class).to(ApiOperationResolverImpl.class).in(Singleton.class);
		
		bind(ApiBuilder.class).to(ApiBuilderImpl.class).in(Singleton.class);
		bind(InstanceProvider.class).to(GuiceInstanceProvider.class).in(Singleton.class);
		
		Multibinder<ApiProvider> apiBinder = Multibinder.newSetBinder(binder(), ApiProvider.class);
		apiBinder.addBinding().to(HealthApi.class).in(Singleton.class);
		
		apiClasses.forEach(apiClass->apiBinder.addBinding().to(apiClass).in(Singleton.class));
	}

}
