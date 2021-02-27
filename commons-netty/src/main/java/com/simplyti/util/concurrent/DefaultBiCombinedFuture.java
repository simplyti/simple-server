package com.simplyti.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.netty.util.concurrent.EventExecutor;

public class DefaultBiCombinedFuture<T,U> implements BiCombinedFuture<T,U>{
	
	private final Future<Object[]> target;

	public DefaultBiCombinedFuture(io.netty.util.concurrent.Future<Object[]> target, EventExecutor loop) {
		this.target=new DefaultFuture<>(target, loop);
	}

	@SuppressWarnings("unchecked")
	public Future<Void> thenAccept(BiConsumer<? super T,? super U> action){
		return target.thenAccept(arr->action.accept((T)arr[0], (U)arr[1]));
	}
	
	@SuppressWarnings("unchecked")
	public <O> Future<O> thenApply(BiFunction<? super T,? super U, ? extends O> action) {
		return target.thenApply(arr->action.apply((T)arr[0], (U)arr[1]));
	}

	@SuppressWarnings("unchecked")
	public Future<T> onError(Consumer<Throwable> action){
		return (Future<T>) target.onError(action);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> Future<O> thenCombine(BiFunction<? super T, ? super U, io.netty.util.concurrent.Future<O>> fn) {
		return target.thenCombine(arr->fn.apply((T)arr[0], (U)arr[1]));
	}

}
