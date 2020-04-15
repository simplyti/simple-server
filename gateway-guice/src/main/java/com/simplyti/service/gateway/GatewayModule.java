package com.simplyti.service.gateway;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.google.inject.TypeLiteral;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.gateway.api.GatewayApi;
import com.simplyti.service.gateway.channel.ChannelFactoryProvider;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;

public class GatewayModule extends AbstractModule{
	
	private final boolean keepOriginalHost;
	private final long maxIddleTime;
	private final int releaseChannelGraceTime;

	public GatewayModule() {
		this(false);
	}
	
	public GatewayModule(boolean keepOriginalHost) {
		this(10,keepOriginalHost);
	}
	
	public GatewayModule(long maxIddleTime, boolean keepOriginalHost) {
		this(maxIddleTime,keepOriginalHost,-1);
	}
	
	public GatewayModule(long maxIddleTime, boolean keepOriginalHost,int releaseChannelGraceTime) {
		this.keepOriginalHost=keepOriginalHost;
		this.maxIddleTime=maxIddleTime;
		this.releaseChannelGraceTime=releaseChannelGraceTime;
	}
	
	@Override
	public void configure() {
		bind(GatewayConfig.class).toInstance(new GatewayConfig(maxIddleTime,keepOriginalHost,releaseChannelGraceTime));
		bind(new TypeLiteral<ChannelFactory<Channel>>() {}).toProvider(ChannelFactoryProvider.class).in(Singleton.class);
		
		bind(DefaultBackendRequestHandler.class).to(GatewayRequestHandler.class);
		
		bind(InternalClient.class).toProvider(ClientProvider.class).in(Singleton.class);
		
		Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().to(GatewayApi.class).in(Singleton.class);
		
		Multibinder.newSetBinder(binder(), HttpRequestFilter.class);
	}

}
