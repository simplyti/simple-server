package com.simplyti.service;

import com.simplyti.service.builder.di.guice.GuiceServiceBuilder;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface Service<T extends Service<T>> {
	
	public EventLoop executor();
	
	public Future<Void> stop();
	
	public Future<T> start();
	
	public Future<Void> stopFuture();
	
	public static GuiceServiceBuilder<DefaultService> builder() {
		return builder(DefaultService.class);
	}
	
	public static <T extends Service<T>> GuiceServiceBuilder<T> builder(Class<T> serviceClass) {
		return new GuiceServiceBuilder<>(serviceClass);
	}
	
}
