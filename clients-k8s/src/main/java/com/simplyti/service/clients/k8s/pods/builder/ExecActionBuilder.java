package com.simplyti.service.clients.k8s.pods.builder;

public interface ExecActionBuilder<T> {

    LifecycleBuilder<T> command(String ...cmd);
}
