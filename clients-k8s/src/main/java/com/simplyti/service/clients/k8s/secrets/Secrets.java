package com.simplyti.service.clients.k8s.secrets;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.secrets.builder.SecretBuilder;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;

public interface Secrets extends Namespaced<Secret,NamespacedSecrets>, K8sApi<Secret>{

	SecretBuilder builder(String namespace);

}
