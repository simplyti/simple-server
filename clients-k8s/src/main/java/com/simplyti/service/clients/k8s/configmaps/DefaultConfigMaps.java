package com.simplyti.service.clients.k8s.configmaps;

import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.api.serializer.json.TypeLiteral;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.k8s.K8sAPI;
import com.simplyti.service.clients.k8s.common.impl.DefaultK8sApi;
import com.simplyti.service.clients.k8s.common.list.KubeList;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;
import com.simplyti.service.clients.k8s.configmaps.domain.ConfigMap;
import io.netty.channel.EventLoopGroup;

public class DefaultConfigMaps extends DefaultK8sApi<ConfigMap> implements ConfigMaps {

    private static final String RESOURCE = "configmaps";
    private static final TypeLiteral<KubeList<ConfigMap>> LIST_TYPE = new TypeLiteral<KubeList<ConfigMap>>() {};
    private static final TypeLiteral<Event<ConfigMap>> EVENT_TYPE = new TypeLiteral<Event<ConfigMap>>() {};

    public DefaultConfigMaps(EventLoopGroup eventLoopGroup, HttpClient http, long timeoutMillis, Json json) {
        super(eventLoopGroup, http, timeoutMillis, json, K8sAPI.V1, RESOURCE, LIST_TYPE, EVENT_TYPE);
    }

    @Override
    public NamespacedConfigMaps namespace(String namespace) {
        return new DefaultNamespacedConfigMaps(eventLoopGroup(), http(), timeoutMillis(), json(), K8sAPI.V1, namespace, RESOURCE, ConfigMap.class, LIST_TYPE, EVENT_TYPE);
    }
}
