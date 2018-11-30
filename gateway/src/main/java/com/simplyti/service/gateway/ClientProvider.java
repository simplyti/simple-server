package com.simplyti.service.gateway;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.gateway.channel.ProxyClientChannelPoolHandler;

import io.netty.channel.EventLoopGroup;

public class ClientProvider implements Provider<InternalClient>{

	@Inject
	private EventLoopGroup eventLoopGroup;
	
	@Override
	public InternalClient get() {
		return new InternalClient(eventLoopGroup,new ProxyClientChannelPoolHandler(),null);
	}

}
