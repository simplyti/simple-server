package com.simplyti.service.clients.k8s.pods.builder;

public class DefaultLifecycleHandlerBuilder<T> implements LifecycleHandlerBuilder<T> {

    private final LifecycleBuilder<T> parent;
    private final HookHandlerHolder holder;

    public DefaultLifecycleHandlerBuilder(final LifecycleBuilder<T> parent, final HookHandlerHolder holder) {
        this.parent = parent;
        this.holder = holder;
    }

    @Override
    public ExecActionBuilder<T> exec() {
        return new DefaultExecActionBuilder<>(this.parent, this.holder);
    }

    @Override
    public HttpGetActionBuilder<T> httpGet() {
        return new DefaultHttpGetActionBuilder<>(this.parent, this.holder);
    }

    @Override
    public TCPSocketActionBuilder<T> tcpSocket() {
        return new DefaultTCPSocketActionBuilder<>(this.parent, this.holder);
    }
}
