package com.simplyti.service.clients.k8s.ingresses.builder;

public interface IngressRulePathBuilder<B> {

	IngressRulePathBuilder<B> backendServiceName(String string);

	IngressRulePathBuilder<B> backendServicePort(int port);
	
	IngressRulePathBuilder<B> backendServicePort(String portName);

	IngressRuleBuilder<B> create();

}
