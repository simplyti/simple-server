package com.simplyti.service.clients.k8s.configmaps.builder;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.Metadata;
import com.simplyti.service.clients.k8s.common.builder.AbstractK8sResourceBuilder;
import com.simplyti.service.clients.k8s.configmaps.domain.ConfigMap;

import java.util.Map;

public class DefaultConfigMapBuilder extends AbstractK8sResourceBuilder<ConfigMapBuilder, ConfigMap> implements ConfigMapBuilder {

    private static final String KIND = "ConfigMap";

    private Map<String, String> data;

    public DefaultConfigMapBuilder(HttpClient client, Json json, K8sAPI api, String namespace, String resource) {
        super(client, json, api, namespace, resource, ConfigMap.class);
    }

    @Override
    public ConfigMapBuilder withData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    @Override
    protected ConfigMap resource(K8sAPI api, Metadata metadata) {
        return new ConfigMap(KIND, api.version(), metadata, data);
    }
}
