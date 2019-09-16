package com.simplyti.service.clients.k8s.pods.builder;

import com.simplyti.service.clients.k8s.pods.domain.LifecycleHookHandler;

public interface LifecycleHandlerHolder {

	void setPreStop(LifecycleHookHandler hook);

	void setPostStart(LifecycleHookHandler hook);

}
