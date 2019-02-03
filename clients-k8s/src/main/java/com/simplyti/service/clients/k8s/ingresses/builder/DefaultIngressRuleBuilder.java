package com.simplyti.service.clients.k8s.ingresses.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.k8s.ingresses.domain.IngressHttp;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressPath;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressRule;

public class DefaultIngressRuleBuilder<T extends IngressRuleHolder<T>> implements IngressRuleBuilder<T> {

	private final T parent;
	
	private List<IngressPath> paths;

	private String host;

	public DefaultIngressRuleBuilder(T parent) {
		this.parent=parent;
	}

	@Override
	public IngressRulePathBuilder<T> withPath() {
		return new DefaultIngressRulePathBuilder<>(this);
	}

	@Override
	public IngressRulePathBuilder<T> withPath(String path) {
		return new DefaultIngressRulePathBuilder<>(this,path);
	}

	@Override
	public IngressRuleBuilder<T> withHost(String host) {
		this.host=host;
		return this;
	}

	@Override
	public T create() {
		return parent.addRule(new IngressRule(host,new IngressHttp(paths)));
	}

	public IngressRuleBuilder<T> addPath(IngressPath ingressPath) {
		if(paths==null) {
			paths = new ArrayList<>();
		}
		this.paths.add(ingressPath);
		return this;
	}

}
