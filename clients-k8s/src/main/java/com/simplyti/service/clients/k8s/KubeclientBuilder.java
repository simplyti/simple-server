package com.simplyti.service.clients.k8s;

import io.netty.channel.EventLoopGroup;

public class KubeclientBuilder {
	
	private EventLoopGroup eventLoopGroup;
	private String server;
	private String token;
	
	public KubeclientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup = eventLoopGroup;
		return this;
	}

	public KubeclientBuilder server(String server) {
		this.server=server;
		return this;
	}
	
	public KubeclientBuilder token(String token) {
		this.token=token;
		return this;
	}
	
	public KubeClient build() {
		return new DefaultKubeClient(eventLoopGroup,server,token);
	}

}
