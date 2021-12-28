package com.simplyti.service.clients.k8s.ingresses.builder;

import com.simplyti.service.clients.k8s.ingresses.domain.IngressBackend;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressPath;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressServiceBackend;
import com.simplyti.service.clients.k8s.ingresses.domain.PathType;
import com.simplyti.service.clients.k8s.ingresses.domain.ServiceBackendPort;

public class DefaultIngressRulePathBuilder<B> implements IngressRulePathBuilder<B> {

	private final IngressRuleBuilder<B> parent;
	private final String path;
	
	private String serviceName;
	private Object servicePort;
	
	public DefaultIngressRulePathBuilder(IngressRuleBuilder<B> parent) {
		this(parent,null);
	}

	public DefaultIngressRulePathBuilder(IngressRuleBuilder<B> parent, String path) {
		this.parent=parent;
		this.path=path;
	}

	@Override
	public IngressRulePathBuilder<B> backendServiceName(String serviceName) {
		this.serviceName=serviceName;
		return this;
	}

	@Override
	public IngressRulePathBuilder<B> backendServicePort(int servicePort) {
		this.servicePort=servicePort;
		return this;
	}
	
	@Override
	public IngressRulePathBuilder<B> backendServicePort(String servicePortName) {
		this.servicePort=servicePortName;
		return this;
	}

	@Override
	public IngressRuleBuilder<B> create() {
		return parent.addPath(new IngressPath(path,new IngressBackend(new IngressServiceBackend(serviceName,port())), PathType.ImplementationSpecific));
	}

	private ServiceBackendPort port() {
		if(servicePort instanceof String) {
			return new ServiceBackendPort((String) servicePort, null);
		} else {
			return new ServiceBackendPort(null, (Integer) servicePort);
		}
	}

}
