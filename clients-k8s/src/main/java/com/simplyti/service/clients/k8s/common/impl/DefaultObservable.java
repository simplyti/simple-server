package com.simplyti.service.clients.k8s.common.impl;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.Observer;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DefaultObservable<T extends K8sResource> implements Observable<T> {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(DefaultObservable.class);
	
	private final EventLoop executor;
	private final List<Observer<T>> observers;
	
	private String index;

	private boolean closed;

	private Channel channel;

	public DefaultObservable(EventLoop executor,String index) {
		this.executor=executor;
		this.index=index;
		this.observers = new ArrayList<>();
	}

	@Override
	public Observable<T> on(Observer<T> observer) {
		if(executor.inEventLoop()){
			addObserver(observer);
		}else{
			executor.submit(()->addObserver(observer));
		}
		return this;
	}

	private void addObserver(Observer<T> observer) {
		observers.add(observer);
	}

	@Override
	public void event(Event<T> event) {
		if(executor.inEventLoop()){
			notifyObservers0(event);
		}else{
			executor.submit(()->notifyObservers0(event));
		}
	}
	
	private void notifyObservers0(Event<T> event) {
		this.index=event.object().metadata().resourceVersion();
		observers.forEach(observer->notifyObservers(observer,event));
	}
	
	private void notifyObservers(Observer<T> observer, Event<T> event) {
		try{
			observer.newEvent(event);
		}catch (Throwable e) {
			log.warn("Error ocurred during kubernetes event handling",e);
		}
	}

	@Override
	public Future<Void> close() {
		this.closed=true;
		return channel.close();
	}

	@Override
	public String index() {
		return this.index;
	}

	@Override
	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void channel(Channel channel) {
		this.channel=channel;
	}

}
