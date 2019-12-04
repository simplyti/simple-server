package com.simplyti.service.clients.k8s.configmaps;

import com.simplyti.service.clients.k8s.common.NamespacedK8sApi;
import com.simplyti.service.clients.k8s.configmaps.builder.ConfigMapBuilder;
import com.simplyti.service.clients.k8s.configmaps.domain.ConfigMap;

public interface NamespacedConfigMaps extends NamespacedK8sApi<ConfigMap> {

    ConfigMapBuilder builder();

}
