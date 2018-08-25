package com.simplyti.service.gateway;

import java.util.Set;

import io.netty.handler.codec.http.HttpMethod;

public interface ServiceDiscovery {

	public BackendService get(String host, HttpMethod method, String path);

	public Set<BackendService> services();

}
