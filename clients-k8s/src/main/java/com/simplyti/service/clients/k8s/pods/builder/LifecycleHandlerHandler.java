package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.LifecycleHookHandler;

public class LifecycleHandlerHandler {
	
	public enum Hook{
		POST_START, PRE_STOP
	}

	private final LifecycleHandlerHolder holder;
	private final Hook hookType;

	public LifecycleHandlerHandler(LifecycleHandlerHolder holder, Hook hookType) {
		this.holder=holder;
		this.hookType=hookType;
	}

	public void setHookHandler(LifecycleHookHandler hook) {
		if(hookType==Hook.PRE_STOP) {
			holder.setPreStop(hook);
		} else {
			holder.setPostStart(hook);
		}
	}

}
