package com.simplyti.service.gateway;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import com.simplyti.service.clients.Endpoint;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DefaultServiceDiscovery implements ServiceDiscovery{
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final Set<BackendService> services = Sets.newTreeSet();

	@Override
	public BackendService get(String host, HttpMethod method, String path) {
		return services.stream()
			.filter(entry -> entry.method() == null || entry.method().equals(method))
			.filter(entry -> entry.host()== null || ( host!=null && entry.host().equals(host)))
			.filter(entry -> entry.pattern() == null || entry.path().equals(path) || entry.pattern().matcher(path).matches())
			.findFirst().orElse(null);
	}
	
	protected void clear(String host, HttpMethod method, String path) {
		Optional<BackendService> foundService = backendService(host,method,path);
		
		foundService.ifPresent(service->{
			foundService.get().clear();
			log.info("Cleared service: {}",foundService.get());
		});
	}
	
	private Optional<BackendService> backendService(String host, HttpMethod method, String path) {
		return services.stream()
		.filter(entry -> entry.method() == method)
		.filter(entry -> entry.host() == host)
		.filter(entry -> entry.path() == path)
		.findFirst();
	}
	
	public void addEndpoint(String host, HttpMethod method, String path, Endpoint endpoint) {
		Optional<BackendService> foundService = backendService(host,method,path);
		
		foundService.ifPresent(service->{
			foundService.get().add(endpoint);
			log.info("Updated service: {}",foundService.get());
		});
	}

	protected void deleteEndpoint(String host, HttpMethod method, String path, Endpoint endpoint) {
		Optional<BackendService> foundService = backendService(host,method,path);
		
		foundService.ifPresent(service->{
			foundService.get().delete(endpoint);
			log.info("Updated service: {}",foundService.get());
		});
	}
	
	protected void addService(BackendService backendService) {
		log.info("Added service: {}",backendService);
		this.services.add(backendService);
	}
	
	protected void removeService(BackendService backendService) {
		log.info("Removed service: {}",backendService);
		this.services.remove(backendService);
	}
	
	public Set<BackendService> services(){
		return services;
	}

}
