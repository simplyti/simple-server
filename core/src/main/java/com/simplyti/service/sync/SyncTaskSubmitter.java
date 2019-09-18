package com.simplyti.service.sync;

import java.util.concurrent.Callable;

import com.simplyti.util.concurrent.Future;

import io.netty.util.concurrent.EventExecutor;

public interface SyncTaskSubmitter {
	
	public Future<Void> submit(EventExecutor executor, VoidCallable task);
	
	public <T> Future<T> submit(EventExecutor executor, Callable<T> task);
	
}
