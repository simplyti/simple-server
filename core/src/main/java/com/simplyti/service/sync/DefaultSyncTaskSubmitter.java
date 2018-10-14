package com.simplyti.service.sync;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultSyncTaskSubmitter implements SyncTaskSubmitter {

private final ExecutorService executor;
	
	@Inject
	public DefaultSyncTaskSubmitter(ExecutorService executor) {
		this.executor=executor;
	}
	
	public Future<Void> submit(EventExecutor executor, VoidCallable task) {
		Promise<Void> promise = executor.newPromise();
		this.executor.submit(new CatcExceptionVoidCall(task,promise));
		return promise;
	}

	public <T> Future<T> submit(EventExecutor executor, Callable<T> task) {
		Promise<T> promise = executor.newPromise();
		this.executor.submit(new CatcExceptionCall<>(task,promise));
		return promise;
	}
	
	private static final class CatcExceptionCall<T> implements Runnable {
		
		private final Callable<T> task;
		private final Promise<T> promise;

		public CatcExceptionCall(Callable<T> task,Promise<T> promise) {
			this.task=task;
			this.promise=promise;
		}

		public void run() {
			try {
				promise.setSuccess(task.call());
			}catch(Throwable cause) {
				promise.setFailure(cause);
			}
		}
	}
	
	private static final class CatcExceptionVoidCall implements Runnable {
		
		private final VoidCallable task;
		private final Promise<Void> promise;

		public CatcExceptionVoidCall(VoidCallable task,Promise<Void> promise) {
			this.task=task;
			this.promise=promise;
		}

		public void run() {
			try {
				task.call();
				promise.setSuccess(null);
			}catch(Throwable cause) {
				promise.setFailure(cause);
			}
		}
	}
	
}
