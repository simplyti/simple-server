package com.simplyti.service.clients.k8s.pods.builder;

public class DefaultLifecycleHandlerBuilder<T> implements LifecycleHandlerBuilder<T> {

    private final LifecycleBuilder<T> parent;
    private final LifecycleHandlerHandler handler;

    public DefaultLifecycleHandlerBuilder(final LifecycleBuilder<T> parent, final LifecycleHandlerHandler handler) {
        this.parent = parent;
        this.handler = handler;
    }

    @Override
    public ExecActionBuilder<T> exec() {
        return new DefaultExecActionBuilder<>(this.parent, this.handler);
    }

    @Override
    public HttpGetActionBuilder<T> httpGet() {
        return new DefaultHttpGetActionBuilder<>(this.parent, this.handler);
    }

    @Override
    public TCPSocketActionBuilder<T> tcpSocket() {
        return new DefaultTCPSocketActionBuilder<>(this.parent, this.handler);
    }
}
