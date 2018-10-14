package com.simplyti.service.sync;

import java.util.concurrent.Callable;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;

public interface SyncTaskSubmitter {
	
	public Future<Void> submit(EventExecutor executor, VoidCallable task);
	
	public <T> Future<T> submit(EventExecutor executor, Callable<T> task);
	
}
