package com.simplyti.service.clients.k8s.ingresses.builder;

import com.simplyti.service.clients.k8s.ingresses.domain.IngressPath;

public interface IngressRuleBuilder<T> {

	IngressRulePathBuilder<T> withPath();
	
	IngressRulePathBuilder<T> withPath(String path);
	
	IngressRuleBuilder<T> withHost(String host);

	T create();

	IngressRuleBuilder<T> addPath(IngressPath ingressPath);

}
