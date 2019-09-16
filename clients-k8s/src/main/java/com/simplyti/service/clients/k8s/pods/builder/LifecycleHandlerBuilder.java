package com.simplyti.service.clients.k8s.pods.builder;

public interface LifecycleHandlerBuilder<T> {

    ExecActionBuilder<T> exec();
    HttpGetActionBuilder<T> httpGet();
    TCPSocketActionBuilder<T> tcpSocket();
}
