package com.simplyti.util.concurrent;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Future<T> extends io.netty.util.concurrent.Future<T> {
	
	<U> Future<U> thenApply(Function<? super T, ? extends U> fn);

	<U> Future<U> thenCombine(Function<? super T, io.netty.util.concurrent.Future<U>> fn);
	
	<O> Future<O> exceptionallyApply(Function<Throwable, ? extends O> fn);

	Future<Void> thenAccept(Consumer<? super T> action);
	
	Future<Void> onError(Consumer<Throwable> action);

}
