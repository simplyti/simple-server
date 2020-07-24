package com.simplyti.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BiCombinedFuture<T, U> {
	
	Future<Void> thenAccept(BiConsumer<? super T,? super U> action);
	
	<O> Future<O> thenApply(BiFunction<? super T,? super U, ? extends O> action);
	
	<O> Future<O> thenCombine(BiFunction<? super T,? super U, io.netty.util.concurrent.Future<O>> fn);

	<O> Future<O> exceptionallyApply(Function<Throwable, ? extends O> fn);
	
	Future<T> onError(Consumer<Throwable> action);

}
