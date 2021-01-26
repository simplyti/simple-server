package com.simplyti.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class DefaultFuture<T> implements Future<T> {

	private final io.netty.util.concurrent.Future<T> target;
	
	private final EventExecutor loop;
	
	public DefaultFuture(final io.netty.util.concurrent.Future<T> target, EventExecutor loop) {
		this.target=target;
		this.loop=loop;
	}
	
	@Override
	public <U> Future<U> thenApply(final Function<? super T, ? extends U> fn) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					return new DefaultFuture<>(loop.newSucceededFuture(fn.apply(target.getNow())), loop);
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
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
						promise.setFailure(cause);
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	
	@Override
	public Future<Void> thenAccept(final Consumer<? super T> action) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					action.accept(target.getNow());
					return new DefaultFuture<>(loop.newSucceededFuture(null), loop);
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
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
						promise.setFailure(cause);
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public Future<Void> handle(BiConsumer<T, Throwable> action) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					action.accept(target.getNow(),null);
					return new DefaultFuture<>(loop.newSucceededFuture(null), loop);
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
				}
			}else {
				try{
					action.accept(null,target.cause());
					return new DefaultFuture<>(loop.newSucceededFuture(null), loop);
				} catch(Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
				}
			}
		} else {
			Promise<Void> promise = loop.newPromise();
			target.addListener(f->{
				if(f.isSuccess()) {
					try{
						action.accept(target.getNow(),null);
						promise.setSuccess(null);
					} catch(Throwable cause) {
						promise.setFailure(cause);
					}
				} else {
					try{
						action.accept(null, target.cause());
						promise.setSuccess(null);
					} catch(Throwable cause) {
						promise.setFailure(cause);
					}
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public <U> Future<U> thenCombine(final Function<? super T, io.netty.util.concurrent.Future<U>> fn){
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
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
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
						promise.setFailure(cause);
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public <U> Future<U> handleCombine(BiFunction<? super T, Throwable, io.netty.util.concurrent.Future<U>> fn) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				try{
					io.netty.util.concurrent.Future<U> result =  fn.apply(target.getNow(),null);
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
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
				}
			}else {
				try{
					io.netty.util.concurrent.Future<U> result =  fn.apply(null,target.cause());
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
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
				}
			}
		} else {
			Promise<U> promise = loop.newPromise();
			target.addListener(f->{
				if(target.isSuccess()) {
					try{
						io.netty.util.concurrent.Future<U> result =  fn.apply(target.getNow(),null);
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
						promise.setFailure(cause);
					}
				} else {
					try{
						io.netty.util.concurrent.Future<U> result =  fn.apply(null,target.cause());
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
						promise.setFailure(cause);
					}
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	private <O> void handleFuture(final io.netty.util.concurrent.Future<O> result, Promise<O> promise) {
		result.addListener(f->{
			if(f.isSuccess()) {
				promise.setSuccess(result.getNow());
			} else {
				promise.setFailure(result.cause());
			}
		});
	}

	@Override
	public <O> Future<O> exceptionallyApply(final Function<Throwable, ? extends O> fn){
		if(target.isDone()) {
			if(target.isSuccess()) {
				return IncompleteFuture.instance();
			}else {
				try{
					return new DefaultFuture<>(loop.newSucceededFuture(fn.apply(target.cause())), loop);
				} catch (Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
				}
			}
		} else {
			Promise<O> promise = loop.newPromise();
			target.addListener(f->{
				if(!f.isSuccess()) {
					try{
						promise.setSuccess(fn.apply(target.cause()));
					} catch(Throwable cause) {
						promise.setFailure(cause);
					}
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public Future<Void> exceptionally(final Consumer<Throwable> consumer){
		if(target.isDone()) {
			if(target.isSuccess()) {
				return IncompleteFuture.instance();
			}else {
				try{
					consumer.accept(target.cause());
					return new DefaultFuture<>(loop.newSucceededFuture(null), loop);
				} catch (Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause), loop);
				}
			}
		} else {
			Promise<Void> promise = loop.newPromise();
			target.addListener(f->{
				if(!f.isSuccess()) {
					try{
						consumer.accept(target.cause());
						promise.setSuccess(null);
					} catch(Throwable cause) {
						promise.setFailure(cause);
					}
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}
	
	@Override
	public Future<T> onError(final Consumer<Throwable> action) {
		if(target.isDone()) {
			if(!target.isSuccess()) {
				try{
					action.accept(target.cause());
					return new DefaultFuture<>(loop.newFailedFuture(target.cause()),loop);
				} catch (Throwable cause) {
					return new DefaultFuture<>(loop.newFailedFuture(cause),loop);
				}
			} else {
				return new DefaultFuture<>(target,loop);
			}
		} else {
			Promise<T> promise = loop.newPromise();
			target.addListener(f->{
				if(!f.isSuccess()) {
					try{
						action.accept(target.cause());
						promise.setFailure(target.cause());
					} catch(Throwable cause) {
						promise.setFailure(cause);
					}
				} else {
					promise.setSuccess(target.getNow());
				}
			});
			return new DefaultFuture<>(promise, loop);
		}
	}


	@Override
	public <A,B> BiCombinedFuture<A,B> thenCombine(final Function<? super T, io.netty.util.concurrent.Future<A>> fn1, final Function<? super T, io.netty.util.concurrent.Future<B>> fn2) {
		if(target.isDone()) {
			if(target.isSuccess()) {
				io.netty.util.concurrent.Future<Object[]> result = aggregate(target.getNow(),fn1,fn2);
				if(result.isDone()) {
					if(result.isSuccess()) {
						return new DefaultBiCombinedFuture<A,B>(loop.newSucceededFuture(result.getNow()), loop);
					} else {
						return new DefaultBiCombinedFuture<A,B>(loop.newFailedFuture(result.cause()), loop);
					}
				} else {
					Promise<Object[]> promise = loop.newPromise();
					handleFuture(result,promise);
					return new DefaultBiCombinedFuture<A,B>(promise, loop);
				}
			}else {
				return new DefaultBiCombinedFuture<A,B>(loop.newFailedFuture(target.cause()), loop);
			}
		} else {
			Promise<Object[]> promise = loop.newPromise();
			target.addListener(f->{
				if(target.isSuccess()) {
					io.netty.util.concurrent.Future<Object[]> result = aggregate(target.getNow(),fn1,fn2);
					if(result.isDone()) {
						if(result.isSuccess()) {
							promise.setSuccess(result.getNow());
						} else {
							promise.setFailure(result.cause());
						}
					} else {
						handleFuture(result,promise);
					}
				} else {
					promise.setFailure(target.cause());
				}
			});
			return new DefaultBiCombinedFuture<A,B>(promise, loop);
		}
	}
	
	private <A,B> io.netty.util.concurrent.Future<Object[]> aggregate(final T value,
			final Function<? super T, io.netty.util.concurrent.Future<A>> fn1,
			final Function<? super T, io.netty.util.concurrent.Future<B>> fn2) {
		if(loop.inEventLoop()) {
			return aggregate(value,null,functionFuture(value, fn1),functionFuture(value, fn2));
		} else {
			Promise<Object[]> result = loop.newPromise();
			loop.execute(()->aggregate(value, result, functionFuture(value, fn1),functionFuture(value, fn2)));
			return result;
		}
	}

	private io.netty.util.concurrent.Future<Object[]> aggregate(final T value, final Promise<Object[]> result, final io.netty.util.concurrent.Future<?>... futures) {
		PromiseCombiner combiner = new PromiseCombiner(loop);
		Promise<Void> aggregated = loop.newPromise();
		for(io.netty.util.concurrent.Future<?> f:futures) {
			combiner.add(f);
		}
		combiner.finish(aggregated);
		if(aggregated.isDone()) {
			if(aggregated.isSuccess()) {
				Object[] arr = new Object[futures.length];
				for(int i=0;i<futures.length;i++) {
					arr[i]=futures[i].getNow();
				}
				if(result!=null) {
					return result.setSuccess(arr);
				} else {
					return  loop.newSucceededFuture(arr);
				}
			} else {
				if(result!=null) {
					return result.setFailure(aggregated.cause());
				} else {
					return loop.newFailedFuture(aggregated.cause());
				}
			}
		} else {
			final Promise<Object[]> promise;
			if(result!=null) {
				promise = result;
			} else {
				promise = loop.newPromise();
			}
			aggregated.addListener(f->{
				if(f.isSuccess()) {
					Object[] arr = new Object[futures.length];
					for(int i=0;i<futures.length;i++) {
						arr[i]=futures[i].getNow();
					}
					promise.setSuccess(arr);
				} else {
					promise.setFailure(f.cause());
				}
			});
			return promise;
		}
	}

	private <O> io.netty.util.concurrent.Future<O> functionFuture(final T value,Function<? super T, io.netty.util.concurrent.Future<O>> fn) {
		try{
			io.netty.util.concurrent.Future<O> future = fn.apply(value);
			if(future.isDone()) {
				if(future.isSuccess()) {
					return loop.newSucceededFuture(future.getNow());
				} else {
					return loop.newFailedFuture(future.cause());
				}
			} else {
				Promise<O> promise = loop.newPromise();
				future.addListener(f->{
					if(f.isSuccess()) {
						promise.setSuccess(future.getNow());
					} else {
						promise.setFailure(f.cause());
					}
				});
				return promise;
			}
		} catch(Throwable cause) {
			return loop.newFailedFuture(cause);
		}
	}

	@Override
	public boolean isSuccess() {
		return target.isSuccess();
	}

	@Override
	public boolean isCancellable() {
		return target.isCancellable();
	}

	@Override
	public Throwable cause() {
		return target.cause();
	}

	@Override
	public io.netty.util.concurrent.Future<T> addListener(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>> listener) {
		return target.addListener(listener) ;
	}

	@Override
	public io.netty.util.concurrent.Future<T> addListeners(@SuppressWarnings("unchecked") GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>>... listeners) {
		return target.addListeners(listeners);
	}

	@Override
	public io.netty.util.concurrent.Future<T> removeListener(GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>> listener) {
		return target.removeListener(listener);
	}

	@Override
	public io.netty.util.concurrent.Future<T> removeListeners(@SuppressWarnings("unchecked") GenericFutureListener<? extends io.netty.util.concurrent.Future<? super T>>... listeners) {
		return target.removeListeners(listeners);
	}

	@Override
	public io.netty.util.concurrent.Future<T> sync() throws InterruptedException {
		return target.sync();
	}

	@Override
	public io.netty.util.concurrent.Future<T> syncUninterruptibly() {
		return target.syncUninterruptibly();
	}

	@Override
	public io.netty.util.concurrent.Future<T> await() throws InterruptedException {
		return target.await();
	}

	@Override
	public io.netty.util.concurrent.Future<T> awaitUninterruptibly() {
		return target.awaitUninterruptibly();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return target.await(timeout, unit);
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return target.await(timeoutMillis);
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		return target.awaitUninterruptibly(timeout, unit);
	}

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		return target.awaitUninterruptibly(timeoutMillis);
	}

	@Override
	public T getNow() {
		return target.getNow();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return target.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return target.isCancelled();
	}

	@Override
	public boolean isDone() {
		return target.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return target.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return target.get(timeout, unit);
	}

}
