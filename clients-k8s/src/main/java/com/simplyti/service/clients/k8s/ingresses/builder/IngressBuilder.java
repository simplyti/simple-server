package com.simplyti.service.clients.k8s.ingresses.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.ingresses.domain.Ingress;

public interface IngressBuilder extends K8sResourceBuilder<IngressBuilder,Ingress>{

	IngressRuleBuilder<? extends IngressBuilder> withRule();

	IngressTlsBuilder withTls();

}
