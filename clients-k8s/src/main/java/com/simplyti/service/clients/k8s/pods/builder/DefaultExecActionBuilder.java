package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.ExecActionHookHandler;
import com.simplyti.service.clients.k8s.pods.domain.LifecycleHookHandler;

public class DefaultExecActionBuilder<T> implements ExecActionBuilder<T> {

    private final LifecycleBuilder<T> parent;
    private final LifecycleHandlerHandler handler;

    public DefaultExecActionBuilder(final LifecycleBuilder<T> parent, final LifecycleHandlerHandler handler) {
        this.parent = parent;
        this.handler = handler;
    }

    @Override
    public LifecycleBuilder<T> command(String... cmd) {
        this.handler.setHookHandler(new LifecycleHookHandler(new ExecActionHookHandler(cmd),null,null));
        return this.parent;
    }
}
