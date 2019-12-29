package com.simplyti.service.gateway;

import org.junit.Before;
import org.junit.Test;

import com.simplyti.service.clients.Schema;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.Endpoint;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.concurrent.ImmediateEventExecutor;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

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
	public void matchOnlyHostSpecificService() throws InterruptedException, ExecutionException {
		Endpoint endpoint = discovery.get("example.com", HttpMethod.GET, "/",ImmediateEventExecutor.INSTANCE).get().get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.1");
		
		endpoint = discovery.get("example.com", HttpMethod.POST, "/",ImmediateEventExecutor.INSTANCE).get().get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.1");
	}
	
	@Test
	public void matchHostMethodAndPathSpecificService() throws InterruptedException, ExecutionException {
		Endpoint endpoint = discovery.get("example.com", HttpMethod.GET, "/resource",ImmediateEventExecutor.INSTANCE).get().get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
		
		endpoint = discovery.get("example.com", HttpMethod.GET, "/resource/",ImmediateEventExecutor.INSTANCE).get().get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
		
		endpoint = discovery.get("example.com", HttpMethod.GET, "/resource/subresource",ImmediateEventExecutor.INSTANCE).get().get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.2");
	}
	
	@Test
	public void matchMoreSpecificPathService() throws InterruptedException, ExecutionException {
		Endpoint endpoint = discovery.get("example2.com", HttpMethod.GET, "/more/specific/path",ImmediateEventExecutor.INSTANCE).get().get().loadBalander().next();
		assertThat(endpoint).isNotNull();
		assertThat(endpoint.address().host()).isEqualTo("127.0.0.4");
		
	}

}
