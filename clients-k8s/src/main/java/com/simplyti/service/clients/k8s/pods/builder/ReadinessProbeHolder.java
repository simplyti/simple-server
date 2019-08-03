package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.Probe;

public interface ReadinessProbeHolder {

	void addReadinessProbe(Probe probe);

}
