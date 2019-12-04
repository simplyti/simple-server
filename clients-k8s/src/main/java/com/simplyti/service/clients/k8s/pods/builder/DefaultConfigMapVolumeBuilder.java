package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.ConfigMapVolume;
import com.simplyti.service.clients.k8s.pods.domain.Volume;

public class DefaultConfigMapVolumeBuilder implements ConfigMapVolumeBuilder {

    private final PodBuilder parent;
    private final VolumeHolder holder;
    private final String name;

    private String configMapName;

    public DefaultConfigMapVolumeBuilder(PodBuilder parent, VolumeHolder holder, String name) {
        this.parent = parent;
        this.name = name;
        this.holder = holder;
    }

    @Override
    public ConfigMapVolumeBuilder withName(String name) {
        this.configMapName = name;
        return this;
    }

    @Override
    public PodBuilder build() {
        holder.addVolume(new Volume(name, new ConfigMapVolume(this.configMapName)));
        return parent;
    }
}
