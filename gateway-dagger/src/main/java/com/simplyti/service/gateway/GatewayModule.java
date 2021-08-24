package com.simplyti.service.gateway;

import javax.inject.Singleton;

import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.GenericClient;
import com.simplyti.service.clients.channel.factory.DefaultChannelFactory;
import com.simplyti.service.gateway.http.HttpGatewayClient;
import com.simplyti.service.gateway.http.HttpGatewayRequestHandler;

import dagger.Module;
import dagger.Provides;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;

@Module
public class GatewayModule {
	
	@Provides
	@Singleton
	public GatewayConfig gatewayConfig() {
		return GatewayConfig.builder().build();
	}
	
	@Provides
	@Singleton
	@HttpGatewayClient
	public GenericClient internalClient(EventLoopGroup eventLoopGroup, GatewayConfig config, ChannelFactory<Channel> channelFactory) {
		return new HttpClientProvider(eventLoopGroup,config,channelFactory).get();
	}
	
	@Provides
	public DefaultBackendRequestHandler defaultBackendRequestHandler(@HttpGatewayClient GenericClient httpGateway, ServiceDiscovery serviceDiscovery, GatewayConfig gatewayConfig) {
		return new HttpGatewayRequestHandler(httpGateway, serviceDiscovery,gatewayConfig);
	}
	
	@Provides
	public ChannelFactory<Channel> channelFactory(EventLoopGroup eventLoopGroup){
		return new DefaultChannelFactory(eventLoopGroup);
	}

}
