package com.simplyti.service.clients.k8s.secrets.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;

public interface SecretBuilder extends K8sResourceBuilder<SecretBuilder,Secret>{

	public SecretBuilder withData(String key, String str);
	public SecretBuilder withType(String type);

}
