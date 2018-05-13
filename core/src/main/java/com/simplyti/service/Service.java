package com.simplyti.service;

import com.simplyti.service.builder.GuiceServiceBuilder;
import com.simplyti.service.builder.ServiceBuilder;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface Service<T extends Service<T>> {
	
	public EventLoop executor();
	
	public Future<Void> stop();
	
	public Future<T> start();
	
	public Future<Void> stopFuture();
	
	public boolean stopping();

	public static ServiceBuilder<DefaultService> builder() {
		return builder(DefaultService.class);
	}
	
	public static <T extends Service<T>> ServiceBuilder<T> builder(Class<T> serviceClass) {
		return new GuiceServiceBuilder<>(serviceClass);
	}

}
