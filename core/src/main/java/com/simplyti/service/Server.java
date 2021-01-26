package com.simplyti.service;

import java.lang.annotation.Annotation;

import io.netty.util.concurrent.Future;

public interface Server {
	
	public Future<Void> stop();
	
	public Future<Server> start();
	
	public Future<Void> stopFuture();
	
	public <O> O instance(Class<O> clazz);

	public <O> O instanceAnnotatedWith(Class<O> clazz, Class<? extends Annotation> ann);
	
}
