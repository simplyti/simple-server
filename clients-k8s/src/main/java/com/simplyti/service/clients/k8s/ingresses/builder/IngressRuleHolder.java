package com.simplyti.service.clients.k8s.ingresses.builder;

import com.simplyti.service.clients.k8s.ingresses.domain.IngressRule;

public interface IngressRuleHolder <T extends IngressRuleHolder<T>> {
	
	T addRule(IngressRule ingressRule);

}
