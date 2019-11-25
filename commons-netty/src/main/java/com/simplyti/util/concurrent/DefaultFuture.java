package com.simplyti.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.experimental.Delegate;

public class DefaultFuture<T> implements Future<T> {

	@Delegate
	private final io.netty.util.concurrent.Future<T> target;
	
	private final EventExecutor loop;
	
	public DefaultFuture(io.netty.util.concurrent.Future<T> target, EventExecutor loop) {
		this.target=target;
		this.loop=loop;
	}
	
	@Override
	public <U> Future<U> thenApply(Function<? super T, ? extends U> fn) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					return new DefaultFuture<>(loop.newSucceededFuture(fn.apply(target.getNow())), loop);
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(new ExecutionException(cause)), loop);
				}
			}else {
				return new DefaultFuture<>(loop.newFailedFuture(target.cause()), loop);
			}
		} else {
			Promise<U> promise = loop.newPromise();
			target.addListener(f->{
				if(f.isSuccess()) {
					try{
						promise.setSuccess(fn.apply(target.getNow()));
					} catch(Throwable cause) {
						promise.setFailure(new ExecutionException(cause));
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	
	@Override
	public Future<Void> thenAccept(Consumer<? super T> action) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					action.accept(target.getNow());
					return new DefaultFuture<>(loop.newSucceededFuture(null), loop);
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(new ExecutionException(cause)), loop);
				}
			}else {
				return new DefaultFuture<>(loop.newFailedFuture(target.cause()), loop);
			}
		} else {
			Promise<Void> promise = loop.newPromise();
			target.addListener(f->{
				if(f.isSuccess()) {
					try{
						action.accept(target.getNow());
						promise.setSuccess(null);
					} catch(Throwable cause) {
						promise.setFailure(new ExecutionException(cause));
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public <U> Future<U> thenCombine(Function<? super T, io.netty.util.concurrent.Future<U>> fn){
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					io.netty.util.concurrent.Future<U> result =  fn.apply(target.getNow());
					if(result.isDone()) {
						if(result.isSuccess()) {
							return new DefaultFuture<>(loop.newSucceededFuture(result.getNow()), loop);
						} else {
							return new DefaultFuture<>(loop.newFailedFuture(result.cause()), loop);
						}
					} else {
						Promise<U> promise = loop.newPromise();
						handleFuture(result,promise);
						return new DefaultFuture<>(promise, loop);
					}
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(new ExecutionException(cause)), loop);
				}
			}else {
				return new DefaultFuture<>(loop.newFailedFuture(target.cause()), loop);
			}
		} else {
			Promise<U> promise = loop.newPromise();
			target.addListener(f->{
				if(target.isSuccess()) {
					try{
						io.netty.util.concurrent.Future<U> result =  fn.apply(target.getNow());
						if(result.isDone()) {
							if(result.isSuccess()) {
								promise.setSuccess(result.getNow());
							} else {
								promise.setFailure(result.cause());
							}
						} else {
							handleFuture(result,promise);
						}
					} catch(Throwable cause) {
						promise.setFailure(new ExecutionException(cause));
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	private <O> void handleFuture(io.netty.util.concurrent.Future<O> result, Promise<O> promise) {
		result.addListener(f->{
			if(f.isSuccess()) {
				promise.setSuccess(result.getNow());
			} else {
				promise.setFailure(result.cause());
			}
		});
	}

	public <O> Future<O> exceptionallyApply(Function<Throwable, ? extends O> fn){
		if(target.isDone()) {
			if(target.isSuccess()) {
				return IncompleteFuture.instance();
			}else {
				try{
					return new DefaultFuture<>(loop.newSucceededFuture(fn.apply(target.cause())), loop);
				} catch (Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(new ExecutionException(cause)), loop);
				}
			}
		} else {
			Promise<O> promise = loop.newPromise();
			target.addListener(f->{
				if(!f.isSuccess()) {
					try{
						promise.setSuccess(fn.apply(target.cause()));
					} catch(Throwable cause) {
						promise.setFailure(new ExecutionException(cause));
					}
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public Future<Void> onError(Consumer<Throwable> action) {
		if(target.isDone()) {
			if(!target.isSuccess()) {
				try{
					action.accept(target.cause());
					return new DefaultFuture<>(loop.newFailedFuture(target.cause()),loop);
				} catch (Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(new ExecutionException(cause)),loop);
				}
			} else {
				return new DefaultFuture<>(loop.newSucceededFuture(null),loop);
			}
		} else {
			Promise<Void> promise = loop.newPromise();
			target.addListener(f->{
				if(!f.isSuccess()) {
					try{
						action.accept(target.cause());
						promise.setFailure(target.cause());
					} catch(Throwable cause) {
						promise.setFailure(new ExecutionException(cause));
					}
				} else {
					promise.setSuccess(null);
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
}
