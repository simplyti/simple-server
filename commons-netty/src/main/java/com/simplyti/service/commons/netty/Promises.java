package com.simplyti.service.commons.netty;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class Promises {

	public static <T> void toPromise(Future<T> future, Promise<T> promise) {
		if(future.isDone()) {
			doneToPromise(future,promise);
		}else {
			future.addListener(f->doneToPromise(future, promise));
		}
	}

	private static <T> void doneToPromise(Future<T> future, Promise<T> promise) {
		if(future.isSuccess()) {
			promise.setSuccess(future.getNow());
		}else {
			promise.setFailure(future.cause());
		}
	}
}
