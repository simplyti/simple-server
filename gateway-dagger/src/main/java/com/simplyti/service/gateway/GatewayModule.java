package com.simplyti.service.gateway;

import javax.inject.Singleton;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.PoolConfig;
import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.gateway.channel.ProxyClientChannelPoolHandler;

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
		return new GatewayConfig(10,false);
	}
	
	@Provides
	@Singleton
	public InternalClient internalClient(EventLoopGroup eventLoopGroup,GatewayConfig config, ChannelFactory<Channel> channelFactory) {
		return new InternalClient(eventLoopGroup,new ProxyClientChannelPoolHandler(),channelFactory,new PoolConfig(config.maxIddle(), null));
	}
	
	@Provides
	public DefaultBackendRequestHandler defaultBackendRequestHandler(InternalClient client, ServiceDiscovery serviceDiscovery, ServerConfig config, GatewayConfig gatewayConfig) {
		return new GatewayRequestHandler(client, serviceDiscovery, config, gatewayConfig);
	}
	
	@Provides
	public ChannelFactory<Channel> channelFactory(EventLoopGroup eventLoopGroup){
		return new ClientChannelFactory(eventLoopGroup);
	}

}
