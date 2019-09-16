package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.Lifecycle;

public interface LifecycleHolder {

    void setLifecycle(Lifecycle lifecycle);
}
