package com.simplyti.service.clients.k8s.services.builder;

import com.simplyti.service.clients.k8s.services.domain.ServicePort;
import com.simplyti.service.clients.k8s.services.domain.ServiceProtocol;

public class DefaultServicePortBuilder<T extends ServicePortHolder<T>> implements ServicePortBuilder<T> {

	private final T parent;
	private int port;
	private String name;
	private Object target;
	private ServiceProtocol protocol;

	public DefaultServicePortBuilder(T parent) {
		this.parent=parent;
	}

	@Override
	public ServicePortBuilder<T> port(int port) {
		this.port=port;
		return this;
	}
	
	@Override
	public ServicePortBuilder<T> name(String name) {
		this.name=name;
		return this;
	}
	
	@Override
	public ServicePortBuilder<T> targetPort(Object port) {
		this.target=port;
		return this;
	}
	
	@Override
	public ServicePortBuilder<T> protocol(ServiceProtocol protocol) {
		this.protocol=protocol;
		return this;
	}

	@Override
	public T create() {
		return parent.addPort(ServicePort.builder().port(port)
				.name(name)
				.targetPort(target)
				.protocol(protocol).build());
	}

}
