package com.simplyti.service.clients.k8s.pods.builder;

public interface TCPSocketActionBuilder<T> {

    TCPSocketActionBuilder<T> host(String host);
    TCPSocketActionBuilder<T> port(int port);
    LifecycleBuilder<T> end();

}
