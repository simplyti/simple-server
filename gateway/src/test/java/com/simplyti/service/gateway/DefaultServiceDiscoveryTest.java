package com.simplyti.service.gateway;

import org.junit.Test;

import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.Schema;

import io.netty.handler.codec.http.HttpMethod;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;

public class DefaultServiceDiscoveryTest {
	
	private static final Schema HTTP = new Schema("http",false,80);

	@Test
	public void testMatch() {
		DefaultServiceDiscovery discovery = new DefaultServiceDiscovery();
		discovery.addService(new BackendService("example.com", null, null,null, null,Collections.singleton(new Endpoint(HTTP,new Address("127.0.0.1",8080)))));
		discovery.addService(new BackendService("example.com", null, "/resource",null,null,Collections.singleton(new Endpoint(HTTP,new Address("127.0.0.2",8080)))));
		
		Endpoint endpoint = discovery.get("example.com", HttpMethod.GET, "/").loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.1");
		
		endpoint = discovery.get("example.com", HttpMethod.GET, "/resource").loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
	}

}
