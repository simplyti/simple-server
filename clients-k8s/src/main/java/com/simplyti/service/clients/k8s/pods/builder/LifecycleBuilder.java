package com.simplyti.service.clients.k8s.pods.builder;

public interface LifecycleBuilder<T> {

    LifecycleHandlerBuilder<T> preStop();
    LifecycleHandlerBuilder<T> postStart();
    ContainerBuilder<T> end();

}
