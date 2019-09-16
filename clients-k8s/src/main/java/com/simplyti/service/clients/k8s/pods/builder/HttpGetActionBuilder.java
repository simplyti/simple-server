package com.simplyti.service.clients.k8s.pods.builder;

public interface HttpGetActionBuilder<T> {

	HttpGetActionBuilder<T> scheme(String scheme);
    HttpGetActionBuilder<T> host(String host);
    HttpGetActionBuilder<T> header(String name, String value);
    HttpGetActionBuilder<T> port(Object port);
    HttpGetActionBuilder<T> path(String path);
    LifecycleBuilder<T> build();


}
