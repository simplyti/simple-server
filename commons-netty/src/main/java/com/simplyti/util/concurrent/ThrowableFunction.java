package com.simplyti.util.concurrent;

public interface ThrowableFunction<T, R> {

	R apply(T t) throws Throwable;
	
}
