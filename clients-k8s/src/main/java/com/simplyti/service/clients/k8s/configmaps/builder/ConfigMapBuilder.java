package com.simplyti.service.clients.k8s.configmaps.builder;

import com.simplyti.service.clients.k8s.common.builder.K8sResourceBuilder;
import com.simplyti.service.clients.k8s.configmaps.domain.ConfigMap;

import java.util.Map;

public interface ConfigMapBuilder extends K8sResourceBuilder<ConfigMapBuilder, ConfigMap> {

    ConfigMapBuilder withData(Map<String, String> data);

}
