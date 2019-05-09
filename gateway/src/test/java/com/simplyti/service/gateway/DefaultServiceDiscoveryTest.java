package com.simplyti.service.gateway;

import org.junit.Before;
import org.junit.Test;

import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.Schema;

import io.netty.handler.codec.http.HttpMethod;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;

public class DefaultServiceDiscoveryTest {
	
	private static final Schema HTTP = new Schema("http",false,80);
	
	DefaultServiceDiscovery discovery;
	
	@Before
	public void start() {
		discovery = new DefaultServiceDiscovery();
		discovery.addService(new BackendService("example.com", null, null,null,false, null,Collections.singleton(new Endpoint(HTTP,new Address("127.0.0.1",8080)))));
		discovery.addService(new BackendService("example.com", null, "/resource",null,false,null,Collections.singleton(new Endpoint(HTTP,new Address("127.0.0.2",8080)))));
		discovery.addService(new BackendService("example2.com", null, "/",null,false,null,Collections.singleton(new Endpoint(HTTP,new Address("127.0.0.3",8080)))));
		discovery.addService(new BackendService("example2.com", null, "/more/specific/path",null,false,null,Collections.singleton(new Endpoint(HTTP,new Address("127.0.0.4",8080)))));
	}

	@Test
	public void matchOnlyHostSpecificService() {
		Endpoint endpoint = discovery.get("example.com", HttpMethod.GET, "/").get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.1");
		
		endpoint = discovery.get("example.com", HttpMethod.POST, "/").get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.1");
	}
	
	@Test
	public void matchHostMethodAndPathSpecificService() {
		Endpoint endpoint = discovery.get("example.com", HttpMethod.GET, "/resource").get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
		
		endpoint = discovery.get("example.com", HttpMethod.GET, "/resource/").get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
		
		endpoint = discovery.get("example.com", HttpMethod.GET, "/resource/subresource").get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
	}
	
	@Test
	public void matchMoreSpecificPathService() {
		Endpoint endpoint = discovery.get("example2.com", HttpMethod.GET, "/more/specific/path").get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.4");
		
	}

}
