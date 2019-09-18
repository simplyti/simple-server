package com.simplyti.service.clients.k8s.common.impl;

import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;

public interface InternalObservable<T extends K8sResource> extends Observable<T> {

	String index();
	boolean isClosed();
	void channel(Channel channel);
	EventExecutor executor();
	void event(Event<T> event);
	void error(Throwable cause);
}
