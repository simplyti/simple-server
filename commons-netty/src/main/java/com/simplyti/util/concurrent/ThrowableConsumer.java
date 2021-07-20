package com.simplyti.util.concurrent;

public interface ThrowableConsumer<T> {

	void accept(T t) throws Throwable;
	
}
