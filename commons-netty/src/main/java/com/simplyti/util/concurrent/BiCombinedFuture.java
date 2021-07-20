package com.simplyti.util.concurrent;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface BiCombinedFuture<T, U> {
	
	Future<Void> thenAccept(BiConsumer<? super T,? super U> action);
	
	<O> Future<O> thenApply(BiFunction<? super T,? super U, ? extends O> action);
	
	<O> Future<O> thenCombine(BiFunction<? super T,? super U, io.netty.util.concurrent.Future<O>> fn);

	Future<T> onError(ThrowableConsumer<Throwable> action);

}
