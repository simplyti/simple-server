package com.simplyti.service.clients.k8s.ingresses.updater;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.updater.AbstractK8sResourceUpdater;
import com.simplyti.service.clients.k8s.common.updater.JsonPatch;
import com.simplyti.service.clients.k8s.ingresses.builder.DefaultIngressRuleBuilder;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressRuleBuilder;
import com.simplyti.service.clients.k8s.ingresses.builder.IngressRuleHolder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressRule;


public class DefaultIngressesUpdater extends AbstractK8sResourceUpdater<Ingress> implements IngressUpdater, IngressRuleHolder<DefaultIngressesUpdater> {

	public DefaultIngressesUpdater(HttpClient client,Json json, K8sAPI api, String namespace, String resource, String name) {
		super(client,json, api, namespace, resource, name, Ingress.class);
	}

	@Override
	public IngressRuleBuilder<? extends IngressUpdater> addRule() {
		return new DefaultIngressRuleBuilder<>(this);
	}
	
	@Override
	public DefaultIngressesUpdater addRule(IngressRule ingressRule) {
		addPatch(JsonPatch.add("/spec/rules/-", ingressRule));
		return this;
	}
	

}
