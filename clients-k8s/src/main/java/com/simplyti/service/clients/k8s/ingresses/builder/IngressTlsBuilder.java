package com.simplyti.service.clients.k8s.ingresses.builder;

public interface IngressTlsBuilder {

	IngressTlsBuilder withSecretName(String secret);

	IngressTlsBuilder withHost(String host);

	IngressBuilder create();

}
