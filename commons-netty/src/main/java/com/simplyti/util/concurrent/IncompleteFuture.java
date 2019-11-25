package com.simplyti.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.util.concurrent.GenericFutureListener;

public class IncompleteFuture<T> implements Future<T>{

	@SuppressWarnings("rawtypes")
	private static final Future INSTANCE = new IncompleteFuture();

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public Throwable cause() {
		return null;
	}

	@Override
	public io.netty.util.concurrent.Future<T> addListener(
			GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>> listener) {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> addListeners(
			@SuppressWarnings("unchecked") GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>>... listeners) {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> removeListener(
			GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>> listener) {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> removeListeners(
			@SuppressWarnings("unchecked") GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>>... listeners) {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> sync() throws InterruptedException {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> syncUninterruptibly() {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> await() throws InterruptedException {
		return this;
	}

	@Override
	public io.netty.util.concurrent.Future<T> awaitUninterruptibly() {
		return this;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return false;
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return false;
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		return false;
	}

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		return false;
	}

	@Override
	public T getNow() {
		return null;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return null;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return null;
	}

	@Override
	public <U> Future<U> thenApply(Function<? super T, ? extends U> fn) {
		return instance();
	}

	@Override
	public Future<Void> thenAccept(Consumer<? super T> action) {
		return instance();
	}

	@Override
	public <U> Future<U> thenCombine(Function<? super T, io.netty.util.concurrent.Future<U>> fn) {
		return instance();
	}

	@Override
	public <O> Future<O> exceptionallyApply(Function<Throwable, ? extends O> fn) {
		return instance();
	}
	
	@Override
	public Future<Void> onError(Consumer<Throwable> action) {
		return instance();
	}

	@SuppressWarnings("unchecked")
	public static <O> Future<O> instance() {
		return INSTANCE;
	}

}
