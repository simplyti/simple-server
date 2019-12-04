package com.simplyti.service.clients.k8s.configmaps;

import com.simplyti.service.clients.k8s.common.K8sApi;
import com.simplyti.service.clients.k8s.common.Namespaced;
import com.simplyti.service.clients.k8s.configmaps.domain.ConfigMap;

public interface ConfigMaps extends Namespaced<ConfigMap, NamespacedConfigMaps>, K8sApi<ConfigMap> {
}
