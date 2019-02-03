package com.simplyti.service.clients.k8s.secrets;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.secrets.builder.SecretBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;

public interface NamespacedSecrets extends NamespacedK8sApi<Secret> {

	SecretBuilder builder();

}
