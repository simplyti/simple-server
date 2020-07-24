package com.simplyti.service.discovery;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.gateway.BackendService;
import com.simplyti.service.gateway.DefaultServiceDiscovery;

import io.netty.handler.codec.http.HttpMethod;

public class TestServiceDiscovery extends DefaultServiceDiscovery {

	private static TestServiceDiscovery INSTANCE;

	@Inject
	private Injector injector;
	
	public static TestServiceDiscovery getInstance() {
		if(INSTANCE==null) {
			INSTANCE = new  TestServiceDiscovery();
		}
		return INSTANCE;
	}
	
	public static void reset() {
		INSTANCE=null;
	}

	public void addService(String host, HttpMethod method, String path, String rewrite, Endpoint endpoint) {
		this.addService(new BackendService(host, method, path,rewrite, false,null, endpoint==null?null:Collections.singletonList(endpoint)));
	}
	
	public void addService(String host, HttpMethod method, String path,String rewrite, Set<HttpRequestFilter> security, Endpoint endpoint) {
		this.addService(new BackendService(host, method, path, rewrite, false,security, endpoint==null?null:Collections.singletonList(endpoint)));
	}

	public <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

}
