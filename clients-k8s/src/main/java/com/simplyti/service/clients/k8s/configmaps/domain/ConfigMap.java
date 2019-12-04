package com.simplyti.service.clients.k8s.configmaps.domain;

import com.dslplatform.json.CompiledJson;
import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.Metadata;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Accessors(fluent=true)
public class ConfigMap extends K8sResource {

    private Map<String, String> data;

    @CompiledJson
    public ConfigMap(String kind, String apiVersion, Metadata metadata, Map<String, String> data) {
        super(kind, apiVersion, metadata);
        this.data = data;
    }

}
