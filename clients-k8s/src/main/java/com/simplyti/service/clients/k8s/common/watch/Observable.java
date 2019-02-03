package com.simplyti.service.clients.k8s.common.watch;

import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

public interface Observable<T extends K8sResource> {

	Observable<T> on(Observer<T> observer);

	Future<Void> close();
	
	void event(Event<T> event);

	String index();

	boolean isClosed();

	void channel(Channel channel);

}
