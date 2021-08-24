package com.simplyti.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface Future<T> extends io.netty.util.concurrent.Future<T> {
	
	Future<Void> thenAccept(Consumer<? super T> action);
	
	Future<Void> handle(BiConsumer<T,Throwable> consumer);
	
	<U> Future<U> thenApply(ThrowableFunction<? super T, ? extends U> fn);
	
	<U> Future<U> thenCombine(ThrowableFunction<? super T, io.netty.util.concurrent.Future<U>> fn);
	
	<A,B> BiCombinedFuture<A,B> thenCombine(ThrowableFunction<? super T, io.netty.util.concurrent.Future<A>> fn1,ThrowableFunction<? super T, io.netty.util.concurrent.Future<B>> fn2);
	
	<U> Future<U> handleCombine(BiFunction<? super T, Throwable, io.netty.util.concurrent.Future<U>> fn);
	
	Future<T> exceptionallyApply(ThrowableFunction<Throwable, ? extends T> fn);
	
	Future<T> exceptionally(final ThrowableConsumer<Throwable> consumer);
	
	Future<T> onError(ThrowableConsumer<Throwable> action);


}
