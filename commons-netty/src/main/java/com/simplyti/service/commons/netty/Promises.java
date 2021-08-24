package com.simplyti.service.commons.netty;

import java.util.function.Consumer;

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
	
	public static <T> void ifSuccessContinue(Future<T> future, Promise<Void> promise, Consumer<? super T> consumer) {
		if(future.isDone()) {
			doneContinue(future,promise,consumer);
		} else {
			future.addListener(f->doneContinue(future, promise,consumer));
		}
	}
	
	private static <T> void doneContinue(Future<T> future, Promise<Void> promise, Consumer<? super T> consumer) {
		if(future.isSuccess()) {
			consumer.accept(future.getNow());
		}else {
			promise.setFailure(future.cause());
		}
	}
	
}
