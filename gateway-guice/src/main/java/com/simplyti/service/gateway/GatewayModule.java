package com.simplyti.service.gateway;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiProvider;
import com.google.inject.TypeLiteral;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.GenericClient;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.gateway.api.GatewayApi;
import com.simplyti.service.gateway.channel.ChannelFactoryProvider;
import com.simplyti.service.gateway.http.HttpGatewayClient;
import com.simplyti.service.gateway.http.HttpGatewayRequestHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;

public class GatewayModule extends AbstractModule{
	
	private final GatewayConfig config;

	public GatewayModule() {
		this(false);
	}
	
	public GatewayModule(boolean keepOriginalHost) {
		this(0,keepOriginalHost, false);
	}
	
	public GatewayModule(long maxIddleTime, boolean keepOriginalHost, boolean clientMonitorEnabled) {
		this(new GatewayConfig(maxIddleTime,keepOriginalHost,clientMonitorEnabled));
	}
	
	public GatewayModule(GatewayConfig config) {
		this.config=config;
	}

	@Override
	public void configure() {
		bind(GatewayConfig.class).toInstance(config);
		bind(new TypeLiteral<ChannelFactory<Channel>>() {}).toProvider(ChannelFactoryProvider.class).in(Singleton.class);
		
		bind(DefaultBackendRequestHandler.class).to(HttpGatewayRequestHandler.class);
		
		bind(GenericClient.class).annotatedWith(HttpGatewayClient.class).toProvider(HttpClientProvider.class).in(Singleton.class);
		
		Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(GatewayApi.class).in(Singleton.class);
		
		Multibinder.newSetBinder(binder(), HttpRequestFilter.class);
	}

}
