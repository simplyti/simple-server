package com.simplyti.service.clients.k8s.pods.builder;

public interface HttpGetActionBuilder<T> {

    HttpGetActionBuilder<T> endpoint(String host);
    HttpGetActionBuilder<T> addHeaders(String name, String value);
    LifecycleBuilder<T> end();


}
