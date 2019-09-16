package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.LifecycleHookHandler;
import com.simplyti.service.clients.k8s.pods.domain.Lifecycle;

public class DefaultLifecycleBuilder<T> implements LifecycleBuilder<T>, LifecycleHandlerHolder {

    private final ContainerBuilder<T> parent;
    private final LifecycleHolder holder;
    
	private LifecycleHookHandler preStop;
	private LifecycleHookHandler postStart;

    public DefaultLifecycleBuilder(final ContainerBuilder<T> parent, final LifecycleHolder holder) {
        this.parent = parent;
        this.holder = holder;
    }

    @Override
    public LifecycleHandlerBuilder<T> preStop() {
        return new DefaultLifecycleHandlerBuilder<T>(this, new LifecycleHandlerHandler(this,LifecycleHandlerHandler.Hook.PRE_STOP));
    }

    @Override
    public LifecycleHandlerBuilder<T> postStart() {
        return new DefaultLifecycleHandlerBuilder<T>(this, new LifecycleHandlerHandler(this,LifecycleHandlerHandler.Hook.POST_START));
    }

    @Override
    public ContainerBuilder<T> build() {
        this.holder.setLifecycle(new Lifecycle(this.postStart,this.preStop));
        return this.parent;
    }

	@Override
	public void setPreStop(LifecycleHookHandler hook) {
		this.preStop=hook;
	}

	@Override
	public void setPostStart(LifecycleHookHandler hook) {
		this.postStart=hook;
	}

}
