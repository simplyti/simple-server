package com.simplyti.service.clients.k8s;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.HttpEndpoint;

import io.netty.channel.EventLoopGroup;

public class KubeclientBuilder {
	
	private EventLoopGroup eventLoopGroup;
	private Endpoint endpoint;
	private String token;
	
	public KubeclientBuilder eventLoopGroup(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup = eventLoopGroup;
		return this;
	}

	public KubeclientBuilder server(String server) {
		this.endpoint=server==null?null:HttpEndpoint.of(server);
		return this;
	}
	
	public KubeclientBuilder withEndpoint(Endpoint endpoint) {
		this.endpoint=endpoint;
		return this;
	}
	
	public KubeclientBuilder token(String token) {
		this.token=token;
		return this;
	}
	
	public KubeClient build() {
		return new DefaultKubeClient(eventLoopGroup,endpoint,token);
	}

}
