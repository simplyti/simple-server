package com.simplyti.service.commons.netty;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

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
	
	public static <T> void ifSuccess(Promise<T> future, Promise<Void> promise, Executor loop, Consumer<? super T> consumer) {
		if(future.isDone()) {
			done(future,promise,loop,consumer);
		} else {
			future.addListener(f->done(future, promise,loop,consumer));
		}
	}
	
	private static <T> void done(Future<T> future, Promise<Void> promise, Executor loop, Consumer<? super T> consumer) {
		if(future.isSuccess()) {
			loop.execute(()->{
				consumer.accept(future.getNow());
				promise.setSuccess(null);
			});
		}else {
			promise.setFailure(future.cause());
		}
	}

	public static <T> void ifSuccess(Future<T> future, Promise<Void> promise, Consumer<? super T> consumer) {
		if(future.isDone()) {
			done(future,promise,consumer);
		} else {
			future.addListener(f->done(future, promise,consumer));
		}
	}
	
	private static <T> void done(Future<T> future, Promise<Void> promise, Consumer<? super T> consumer) {
		if(future.isSuccess()) {
			consumer.accept(future.getNow());
			promise.setSuccess(null);
		}else {
			promise.setFailure(future.cause());
		}
	}
	
	public static <T, O> void ifSuccessMap(Future<T> future, Promise<O> promise, Executor loop, Function<? super T, ? extends O> fn) {
		if(future.isDone()) {
			doneMap(future,promise,loop,fn);
		} else {
			future.addListener(f->doneMap(future, promise,loop,fn));
		}
	}
	
	private static <T, O> void doneMap(Future<T> future, Promise<O> promise, Executor loop, Function<? super T, ? extends O> fn) {
		if(future.isSuccess()) {
			loop.execute(()->promise.setSuccess(fn.apply(future.getNow())));
		}else {
			promise.setFailure(future.cause());
		}
	}

	public static <T, O> void ifSuccessMap(Future<T> future, Promise<O> promise, Function<? super T, ? extends O> fn) {
		if(future.isDone()) {
			doneMap(future,promise,fn);
		} else {
			future.addListener(f->doneMap(future, promise,fn));
		}
	}

	private static <T, O> void doneMap(Future<T> future, Promise<O> promise, Function<? super T, ? extends O> fn) {
		if(future.isSuccess()) {
			promise.setSuccess(fn.apply(future.getNow()));
		}else {
			promise.setFailure(future.cause());
		}
	}

	public static <T> void ifFailureMap(Promise<?> future, Promise<T> promise, Function<Throwable, ? extends T> fn) {
		if(future.isDone()) {
			failMap(future,promise,fn);
		} else {
			future.addListener(f->failMap(future, promise,fn));
		}
	}
	
	private static <T> void failMap(Future<?> future, Promise<T> promise, Function<Throwable, ? extends T> fn) {
		if(future.isSuccess()) {
			promise.cancel(false);
		}else {
			promise.setSuccess(fn.apply(future.cause()));
		}
	}

}
