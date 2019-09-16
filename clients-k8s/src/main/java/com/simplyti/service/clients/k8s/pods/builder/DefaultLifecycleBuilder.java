package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.Lifecycle;

public class DefaultLifecycleBuilder<T> implements LifecycleBuilder<T> {

    private final ContainerBuilder<T> parent;
    private final LifecycleHolder holder;
    private final HookHandlerHolder preStopHolder;
    private final HookHandlerHolder postStartHolder;

    public DefaultLifecycleBuilder(final ContainerBuilder<T> parent, final LifecycleHolder holder) {
        this.parent = parent;
        this.holder = holder;
        this.preStopHolder = new HookHandlerHolder();
        this.postStartHolder = new HookHandlerHolder();
    }

    @Override
    public LifecycleHandlerBuilder<T> preStop() {
        return new DefaultLifecycleHandlerBuilder<T>(this, this.preStopHolder);
    }

    @Override
    public LifecycleHandlerBuilder<T> postStart() {
        return new DefaultLifecycleHandlerBuilder<T>(this, this.postStartHolder);
    }

    @Override
    public ContainerBuilder<T> end() {
        this.holder.setLifecycle(new Lifecycle(this.preStopHolder.handler(), this.postStartHolder.handler()));
        return this.parent;
    }

}
