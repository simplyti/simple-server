package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.TCPSocketActionHandler;

public class DefaultTCPSocketActionBuilder<T> implements TCPSocketActionBuilder<T> {

    private final LifecycleBuilder<T> parent;
    private final HookHandlerHolder holder;

    private String host;
    private int port;

    public DefaultTCPSocketActionBuilder(final LifecycleBuilder<T> parent, final HookHandlerHolder holder) {
        this.parent = parent;
        this.holder = holder;
    }

    @Override
    public TCPSocketActionBuilder<T> host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public TCPSocketActionBuilder<T> port(int port) {
        this.port = port;
        return this;
    }

    @Override
    public LifecycleBuilder<T> end() {
        this.holder.setHookHandler(new TCPSocketActionHandler(this.host, this.port));
        return this.parent;
    }
}
