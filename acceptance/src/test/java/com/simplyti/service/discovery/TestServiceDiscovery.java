package com.simplyti.service.discovery;

import java.util.Collections;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.gateway.BackendService;
import com.simplyti.service.gateway.DefaultServiceDiscovery;

import io.netty.handler.codec.http.HttpMethod;

public class TestServiceDiscovery extends DefaultServiceDiscovery {

	private static TestServiceDiscovery INSTANCE;

	public static TestServiceDiscovery getInstance() {
		if(INSTANCE==null) {
			INSTANCE = new  TestServiceDiscovery();
		}
		return INSTANCE;
	}
	
	public static void reset() {
		INSTANCE=null;
	}

	public void addService(String host, HttpMethod method, String path, Endpoint endpoint) {
		this.addService(new BackendService(host, method, path, endpoint==null?null:Collections.singleton(endpoint)));
	}

}
