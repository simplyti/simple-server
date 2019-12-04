package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors
public class ConfigMapVolume {

    private final String name;

    @CompiledJson
    public ConfigMapVolume(String name) {
        this.name = name;
    }
}
