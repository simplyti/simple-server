package com.simplyti.service.clients.k8s.ingresses.builder;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.k8s.ingresses.domain.IngressTls;

public class DefaultIngressTlsBuilder implements IngressTlsBuilder {

	private final DefaultIngressBuilder parent;
	
	private String secret;
	private List<String> hosts;

	public DefaultIngressTlsBuilder(DefaultIngressBuilder parent) {
		this.parent=parent;
	}

	@Override
	public IngressTlsBuilder withSecretName(String secret) {
		this.secret=secret;
		return this;
	}

	@Override
	public IngressTlsBuilder withHost(String host) {
		if(hosts==null) {
			this.hosts=new ArrayList<>();
		}
		this.hosts.add(host);
		return this;
	}

	@Override
	public IngressBuilder create() {
		return parent.addTls(new IngressTls(hosts,secret));
	}

}
