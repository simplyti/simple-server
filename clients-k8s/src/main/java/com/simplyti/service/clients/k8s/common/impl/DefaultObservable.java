package com.simplyti.service.clients.k8s.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.simplyti.service.clients.k8s.common.K8sResource;
import com.simplyti.service.clients.k8s.common.watch.Observable;
import com.simplyti.service.clients.k8s.common.watch.domain.Event;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class DefaultObservable<T extends K8sResource> implements Observable<T>, InternalObservable<T> {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(DefaultObservable.class);
	
	private final EventLoop executor;
	private final List<Consumer<Event<T>>> observers;
	private final List<Consumer<Throwable>> errorConsumers;
	
	private String index;

	private boolean closed;

	private Channel channel;

	public DefaultObservable(EventLoop executor,String index) {
		this.executor=executor;
		this.index=index;
		this.observers = new ArrayList<>();
		this.errorConsumers = new ArrayList<>();
	}

	@Override
	public Observable<T> onEvent(Consumer<Event<T>> consumer) {
		if(executor.inEventLoop()){
			addObserver(consumer);
		}else{
			executor.submit(()->addObserver(consumer));
		}
		return this;
	}
	
	private void addObserver(Consumer<Event<T>> consumer) {
		observers.add(consumer);
	}
	
	@Override
	public Observable<T> onError(Consumer<Throwable> consumer) {
		if(executor.inEventLoop()){
			addErrorConsumer(consumer);
		}else{
			executor.submit(()->addErrorConsumer(consumer));
		}
		return this;
	}

	private void addErrorConsumer(Consumer<Throwable> consumer) {
		errorConsumers.add(consumer);
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
	
	private void notifyObservers(Consumer<Event<T>> observer, Event<T> event) {
		try{
			observer.accept(event);
		}catch (Throwable e) {
			log.warn("Error ocurred during kubernetes event handling",e);
		}
	}
	
	@Override
	public void error(Throwable cause) {
		if(executor.inEventLoop()){
			notifyErrors0(cause);
		}else{
			executor.submit(()->notifyErrors0(cause));
		}
	}

	private void notifyErrors0(Throwable cause) {
		errorConsumers.forEach(consumer->notifyErrors(consumer,cause));
	}

	private void notifyErrors(Consumer<Throwable> consumer, Throwable cause) {
		try{
			consumer.accept(cause);
		}catch (Throwable e) {
			log.warn("Error ocurred during error handling",e);
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

	@Override
	public EventExecutor executor() {
		return this.executor;
	}

}
