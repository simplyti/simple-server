package com.simplyti.service.clients.k8s.configmaps;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultNamespacedK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.configmaps.builder.ConfigMapBuilder;
import com.simplyti.service.clients.k8s.configmaps.builder.DefaultConfigMapBuilder;
import com.simplyti.service.clients.k8s.configmaps.domain.ConfigMap;
import io.netty.channel.EventLoopGroup;

public class DefaultNamespacedConfigMaps extends DefaultNamespacedK8sApi<ConfigMap> implements NamespacedConfigMaps {

    public DefaultNamespacedConfigMaps(EventLoopGroup eventLoopGroup, HttpClient http, Json json, K8sAPI api, String namespace, String resource, Class<ConfigMap> type, TypeLiteral<KubeList<ConfigMap>> listType, TypeLiteral<Event<ConfigMap>> eventType) {
        super(eventLoopGroup, http, json, api, namespace, resource, type, listType, eventType);
    }

    @Override
    public ConfigMapBuilder builder() {
        return new DefaultConfigMapBuilder(http(),json(), api(), namespace(), resource());
    }
}
