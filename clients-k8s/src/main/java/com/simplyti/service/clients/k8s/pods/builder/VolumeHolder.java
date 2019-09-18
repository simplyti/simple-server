package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.Volume;

public interface VolumeHolder {

	void addVolume(Volume volume);

}
