package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.ExecActionHookHandler;

public class DefaultExecActionBuilder<T> implements ExecActionBuilder<T> {

    private final LifecycleBuilder<T> parent;
    private final HookHandlerHolder holder;

    public DefaultExecActionBuilder(final LifecycleBuilder<T> parent, final HookHandlerHolder holder) {
        this.parent = parent;
        this.holder = holder;
    }

    @Override
    public LifecycleBuilder<T> command(String... cmd) {
        this.holder.setHookHandler(new ExecActionHookHandler(cmd));
        return this.parent;
    }
}
