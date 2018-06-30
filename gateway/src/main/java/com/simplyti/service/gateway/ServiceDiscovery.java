package com.simplyti.service.gateway;

import java.util.Set;

import com.simplyti.service.gateway.balancer.ServiceBalancer;

import io.netty.handler.codec.http.HttpMethod;

public interface ServiceDiscovery {

	public ServiceBalancer get(String host, HttpMethod method, String path);

	public Set<BackendService> services();

}
