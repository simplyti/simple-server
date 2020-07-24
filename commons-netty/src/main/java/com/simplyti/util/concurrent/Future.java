package com.simplyti.util.concurrent;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Future<T> extends io.netty.util.concurrent.Future<T> {
	
	Future<Void> thenAccept(Consumer<? super T> action);
	
	<U> Future<U> thenApply(Function<? super T, ? extends U> fn);
	
	<U> Future<U> thenCombine(Function<? super T, io.netty.util.concurrent.Future<U>> fn);
	
	<A,B> BiCombinedFuture<A,B> thenCombine(Function<? super T, io.netty.util.concurrent.Future<A>> fn1,Function<? super T, io.netty.util.concurrent.Future<B>> fn2);
	
	<O> Future<O> exceptionallyApply(Function<Throwable, ? extends O> fn);
	
	Future<T> onError(Consumer<Throwable> action);
	
}
