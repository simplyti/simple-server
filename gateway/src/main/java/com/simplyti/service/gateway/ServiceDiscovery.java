package com.simplyti.service.gateway;

import java.util.Set;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

public interface ServiceDiscovery {

	public Future<BackendServiceMatcher> get(String host, HttpMethod method, String path, EventExecutor eventLoop);

	public Set<BackendService> services();

}
