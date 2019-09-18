package com.simplyti.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.simplyti.service.commons.netty.Promises;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public class DefaultFuture<T> implements Future<T>, Promise<T> {

	private final Promise<T> target;
	
	private final EventExecutor loop;
	
	public DefaultFuture(Promise<T> target, EventExecutor loop) {
		this.target=target;
		this.loop=loop;
	}
	
	@Override
	public <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn) {
		DefaultFuture<U> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccessMap(target, next, fn);
		return next;
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		DefaultFuture<U> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccessMap(target, next, loop, fn);
		return next;
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
		DefaultFuture<U> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccessMap(target, next, executor, fn);
		return next;
	}

	@Override
	public CompletionStage<Void> thenAccept(Consumer<? super T> action) {
		DefaultFuture<Void> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccess(target, next, action);
		return next;
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
		DefaultFuture<Void> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccess(target, next, loop, action);
		return next;
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		DefaultFuture<Void> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccess(target, next, executor, action);
		return next;
	}

	@Override
	public CompletionStage<Void> thenRun(Runnable action) {
		DefaultFuture<Void> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccess(target, next, ignore->action.run());
		return next;
	}

	@Override
	public CompletionStage<Void> thenRunAsync(Runnable action) {
		DefaultFuture<Void> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccess(target, next, loop, ignore->action.run());
		return next;
	}

	@Override
	public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {
		DefaultFuture<Void> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifSuccess(target, next, executor, ignore->action.run());
		return next;
	}

	@Override
	public <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
		DefaultFuture<T> next = new DefaultFuture<>(loop.newPromise(), loop);
		Promises.ifFailureMap(target,next,fn);
		return next;
	}

	@Override
	public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<T> toCompletableFuture() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSuccess() {
		return this.target.isSuccess();
	}

	@Override
	public boolean isCancellable() {
		return this.target.isCancellable();
	}

	@Override
	public Throwable cause() {
		return this.target.cause();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return this.target.await(timeout,unit);
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return this.target.await(timeoutMillis);
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		return this.target.awaitUninterruptibly(timeout, unit);
	}

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		return this.target.awaitUninterruptibly(timeoutMillis);
	}

	@Override
	public T getNow() {
		return this.target.getNow();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return this.target.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return this.target.isCancelled();
	}

	@Override
	public boolean isDone() {
		return this.target.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return this.target.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return this.get(timeout, unit);
	}

	@Override
	public Promise<T> setSuccess(T result) {
		return this.target.setSuccess(result);
	}

	@Override
	public boolean trySuccess(T result) {
		return this.target.trySuccess(result);
	}

	@Override
	public Promise<T> setFailure(Throwable cause) {
		return this.target.setFailure(cause);
	}

	@Override
	public boolean tryFailure(Throwable cause) {
		return this.target.tryFailure(cause);
	}

	@Override
	public boolean setUncancellable() {
		return this.target.setUncancellable();
	}

	@Override
	public Promise<T> addListener(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>> listener) {
		return this.target.addListener(listener);
	}

	@Override
	public Promise<T> addListeners(@SuppressWarnings("unchecked") GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>>... listeners) {
		return this.target.addListeners(listeners);
	}

	@Override
	public Promise<T> removeListener(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>> listener) {
		return this.target.removeListener(listener);
	}

	@Override
	public Promise<T> removeListeners(@SuppressWarnings("unchecked") GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>>... listeners) {
		return this.target.removeListeners(listeners);
	}

	@Override
	public Promise<T> await() throws InterruptedException {
		return this.target.await();
	}

	@Override
	public Promise<T> awaitUninterruptibly() {
		return this.target.awaitUninterruptibly();
	}

	@Override
	public Promise<T> sync() throws InterruptedException {
		return this.target.sync();
	}

	@Override
	public Promise<T> syncUninterruptibly() {
		return this.target.syncUninterruptibly();
	}
	
}
