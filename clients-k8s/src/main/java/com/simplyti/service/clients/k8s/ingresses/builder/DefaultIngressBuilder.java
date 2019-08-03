package com.simplyti.service.clients.k8s.ingresses.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressRule;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressSpec;
import com.simplyti.service.clients.k8s.ingresses.domain.IngressTls;


public class DefaultIngressBuilder extends AbstractK8sResourceBuilder<IngressBuilder,Ingress> implements IngressBuilder, IngressRuleHolder<DefaultIngressBuilder> {
	
	public static final String KIND = "Ingress";
	
	private List<IngressRule> rules;
	private List<IngressTls> tlss;

	public DefaultIngressBuilder(HttpClient client,Json json, K8sAPI api,String namespace, String resource) {
		super(client,json,api,namespace,resource,Ingress.class);
	}

	@Override
	public IngressRuleBuilder<? extends IngressBuilder> withRule() {
		return new DefaultIngressRuleBuilder<>(this);
	}

	@Override
	public IngressTlsBuilder withTls() {
		return new DefaultIngressTlsBuilder(this);
	}

	@Override
	protected Ingress resource(K8sAPI api, Metadata metadata) {
		return new Ingress(KIND, api.version(), metadata, new IngressSpec(rules, tlss));
	}

	public DefaultIngressBuilder addRule(IngressRule ingressRule) {
		if(rules==null) {
			this.rules=new ArrayList<>();
		}
		rules.add(ingressRule);
		return this;
	}

	public IngressBuilder addTls(IngressTls tls) {
		if(tlss==null) {
			this.tlss=new ArrayList<>();
		}
		tlss.add(tls);
		return this;
	}

}
