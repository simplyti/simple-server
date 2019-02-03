package com.simplyti.service.clients.k8s.common.watch;

import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

@FunctionalInterface
public interface Observer<T extends K8sResource> {
	
	void newEvent(Event<T> event);

}

