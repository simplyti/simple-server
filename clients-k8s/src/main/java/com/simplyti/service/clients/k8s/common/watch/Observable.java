package com.simplyti.service.clients.k8s.common.watch;

import java.util.function.Consumer;

import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.util.concurrent.Future;

public interface Observable<T extends K8sResource> {

	Observable<T> onEvent(Consumer<Event<T>> observer);
	Observable<T> onError(Consumer<Throwable> object);
	Future<Void> close();
	
}
