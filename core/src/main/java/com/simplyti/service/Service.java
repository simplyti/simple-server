package com.simplyti.service;

import com.simplyti.service.builder.GuiceServiceBuilder;
import com.simplyti.service.builder.ServiceBuilder;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public interface Service {
	
	public EventLoop executor();
	
	public Future<Void> stop();
	
	public Future<Service> start();
	
	public Future<Void> stopFuture();
	
	public boolean stopping();

	public static ServiceBuilder builder() {
		return new GuiceServiceBuilder();
	}

}
