package com.simplyti.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultFutureTest {
	
	private EventLoopGroup eventloopGroup;
	private Promise<String> target;
	private Future<String> future;
	private EventLoop loop;

	@Before
	public void setup() {
		this.eventloopGroup=new NioEventLoopGroup(20);
		this.loop = eventloopGroup.next();
		this.target = loop.newPromise();
		this.future = new DefaultFuture<>(target, loop);
	}
	
	@After
	public void stop() {
		this.eventloopGroup.shutdownGracefully();
	}
	
	@Test
	public void testAsyncApplyAndAccept() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenApply(v->v.concat(" Pepe"))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe"));
	}
	
	@Test
	public void testSyncApplyAndAccept() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenApply(v->v.concat(" Pepe"))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe"));
	}
	
	@Test
	public void testAsyncAcceptButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.thenAccept(result::set)
			.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncAcceptButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.thenAccept(result::set)
			.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncExceptionApplyAndAccept() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.exceptionallyApply(Throwable::getMessage)
			.thenApply(String::toUpperCase)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("ERROR!"));
	}
	
	@Test
	public void testSyncExceptionApplyAndAccept() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.exceptionallyApply(Throwable::getMessage)
			.thenApply(String::toUpperCase)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("ERROR!"));
	}
	
	@Test
	public void testAsyncCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(this::concatAsync)
			.thenAccept(r->result.set(r))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe"));
	}
	
	@Test
	public void testAsyncCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(v->eventloopGroup.next().newSucceededFuture(v.concat(" Pepe")))
			.thenAccept(r->result.set(r))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe"));
	}
	
	@Test
	public void testSyncCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenCombine(v->eventloopGroup.next().newSucceededFuture(v.concat(" Pepe")))
			.thenAccept(r->result.set(r))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe"));
	}
	
	@Test
	public void testSyncCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenCombine(this::concatAsync)
			.thenAccept(r->result.set(r))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe"));
	}
	
	@Test
	public void testAsyncCombineButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(this::concatAsync)
		.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncCombineButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.thenCombine(this::concatAsync)
		.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncCombineSyncButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenCombine(v->eventloopGroup.next().newFailedFuture(new RuntimeException("Error!")))
			.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncCombineAsyncButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenCombine(v->errorAsync("Error!"))
			.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncCombineSyncButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(v->eventloopGroup.next().newFailedFuture(new RuntimeException("Error!")))
			.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncCombineAsyncButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(v->errorAsync("Error!"))
			.onError(error->result.set(error.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncOnError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),nullValue());
	}
	
	@Test
	public void testAsyncOnError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),nullValue());
	}
	
	@Test
	public void testAsyncExceptionApply() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.exceptionallyApply(Throwable::getMessage)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncExceptionApply() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.exceptionallyApply(Throwable::getMessage)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncExceptionApplyNoError() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.exceptionallyApply(Throwable::getMessage)
			.thenAccept(result::set)
			.await(500,TimeUnit.MILLISECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testSyncExceptionApplyNoError() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.exceptionallyApply(Throwable::getMessage)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testAsyncApplyError() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.thenApply(String::toUpperCase)
			.exceptionallyApply(Throwable::getMessage)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncApplyError() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.thenApply(String::toUpperCase)
			.exceptionallyApply(Throwable::getMessage)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncApplyHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenApply(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testAsyncApplyHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenApply(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testSyncAcceptHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenAccept(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testAsyncAcceptHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenAccept(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testSynCombineHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.thenCombine(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testAsynCombineHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testSynExceptionApplyHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Original error"));
		future.exceptionallyApply(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testAsynExceptionApplyHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Original error")), 10, TimeUnit.MILLISECONDS);
		future.exceptionallyApply(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testSynOnErrorHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Original error"));
		future.onError(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testAsynOnErrorHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Original error")), 10, TimeUnit.MILLISECONDS);
		future.onError(e-> {throw new RuntimeException("Error");})
			.onError(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	@Test
	public void testAsyncBiCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(this::concatAsync,this::hashCodeAsync)
			.thenAccept((a,b)->result.set(a+" - "+b))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe - 69609650"));
	}
	
	@Test
	public void testAsyncBiCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.thenCombine(
				v->eventloopGroup.next().newSucceededFuture(v.concat(" Pepe")),
				v->eventloopGroup.next().newSucceededFuture(v.hashCode()))
			.thenAccept((a,b)->result.set(a+" - "+b))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe - 69609650"));
	}
	
	@Test
	public void testSyncBiCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		inLoop(()->future
					.thenCombine(
						v->loop.newSucceededFuture(v.concat(" Pepe")),
						v->loop.newSucceededFuture("!"))
					.thenAccept((a,b)->result.set(a+b)))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe!"));
	}
	
	@Test
	public void testSyncBiCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		inLoop(()->future
					.thenCombine(
						this::concatAsync,
						v->loop.newSucceededFuture("!"))
					.thenAccept((a,b)->result.set(a+b)))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe!"));
	}
	
	@Test
	public void testSyncBiCombineAsyncOutOfLoop() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future
			.thenCombine(
				this::concatAsync,
				v->loop.newSucceededFuture("!"))
			.thenAccept((a,b)->result.set(a+b))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe!"));
	}
	
	@Test
	public void testSyncBiCombineSyncOutOfLoop() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future
			.thenCombine(
				v->loop.newSucceededFuture(v.concat(" Pepe")),
				v->loop.newSucceededFuture("!"))
			.thenAccept((a,b)->result.set(a+b))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello Pepe!"));
	}
	
	@Test
	public void testSyncBiCombineSyncHandleError() throws InterruptedException {
		AtomicReference<Throwable> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future
			.thenCombine(
				v->{throw new RuntimeException("Error");},
				v->loop.newSucceededFuture("!"))
			.onError(result::set)
			.await(5,TimeUnit.SECONDS);
		
		assertThat(result.get(),instanceOf(RuntimeException.class));
		assertThat(((Exception)result.get()).getMessage(),equalTo("Error"));
	}
	
	private io.netty.util.concurrent.Future<Void> inLoop(Supplier<Future<?>> fn) {
		Promise<Void> promise = loop.newPromise();
		loop.execute(()->fn.get()
				.thenAccept(f->promise.setSuccess(null))
				.onError(promise::setFailure));
		return promise;
	}
	
	private io.netty.util.concurrent.Future<String> async(String value) {
		Promise<String> promise = eventloopGroup.next().newPromise();
		eventloopGroup.next().schedule(()->promise.setSuccess(value), 10, TimeUnit.MILLISECONDS);
		return promise;
	}

	private io.netty.util.concurrent.Future<String> concatAsync(String value) {
		Promise<String> promise = eventloopGroup.next().newPromise();
		eventloopGroup.next().schedule(()->promise.setSuccess(value.concat(" Pepe")), 10, TimeUnit.MILLISECONDS);
		return promise;
	}
	
	private io.netty.util.concurrent.Future<Integer> hashCodeAsync(String value) {
		Promise<Integer> promise = eventloopGroup.next().newPromise();
		eventloopGroup.next().schedule(()->promise.setSuccess(value.hashCode()), 10, TimeUnit.MILLISECONDS);
		return promise;
	}
	
	private io.netty.util.concurrent.Future<String> errorAsync(String err) {
		Promise<String> promise = eventloopGroup.next().newPromise();
		eventloopGroup.next().schedule(()->promise.setFailure(new RuntimeException(err)), 10, TimeUnit.MILLISECONDS);
		return promise;
	}
	
	@Test
	public void testAsyncHandle() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.handle((v,err)->result.set(v))
		.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testAsyncHandleButExceptionOnHandle() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.handle((v,err)->{throw new RuntimeException("Handle Error");})
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Handle Error"));
	}
	
	@Test
	public void testAsyncHandleButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.handle((v,err)->result.set(err.getMessage()))
		.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncHandleButExceptionOnExceptionHandle() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.handle((v,err)->{throw new RuntimeException("Handle Error");})
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Handle Error"));
	}
	
	@Test
	public void testSyncHandle() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.handle((v,err)->result.set(v))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testSyncHandleButExceptionOnHandle() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.handle((v,err)->{throw new RuntimeException("Handle Error");})
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Handle Error"));
	}
	
	@Test
	public void testSyncHandleButException() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.handle((v,err)->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncHandleButExceptionOnExceptionHandle() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.handle((v,err)->{throw new RuntimeException("Handle Error");})
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Handle Error"));
	}
	
	
//
	@Test
	public void testAsyncHandleCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.handleCombine((v,err)->async(v))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testAsyncHandleCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.handleCombine((v,err)->loop.newSucceededFuture(v))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testAsyncHandleCombineButExceptionAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.handleCombine((v,err)->errorAsync("Error!"))
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncHandleCombineButExceptionSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello"), 10, TimeUnit.MILLISECONDS);
		future.handleCombine((v,err)->loop.newFailedFuture(new RuntimeException("Error!")))
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncHandleCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.handleCombine((v,err)->async(v))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testSyncHandleCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.handleCombine((v,err)->loop.newSucceededFuture(v))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello"));
	}
	
	@Test
	public void testSyncHandleCombineButExceptionAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.handleCombine((v,err)->errorAsync("Error!"))
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncHandleCombineButExceptionSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello");
		future.handleCombine((v,err)->loop.newFailedFuture(new RuntimeException("Error!")))
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncExceptionHandleCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.handleCombine((v,err)->async(err.getMessage()))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testAsyncExceptionHandleCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.handleCombine((v,err)->loop.newFailedFuture(new RuntimeException("New Error!")))
		.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("New Error!"));
	}
	
	@Test
	public void testSyncExceptionHandleCombineAsync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.handleCombine((v,err)->async(err.getMessage()))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncExceptionHandleCombineSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.handleCombine((v,err)->loop.newSucceededFuture(err.getMessage()))
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncExceptionHandleCombineButExceptionSync() throws InterruptedException {
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.handleCombine((v,err)->loop.newFailedFuture(new RuntimeException("Error!")))
			.exceptionally(err->result.set(err.getMessage()))
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Error!"));
	}
	
	@Test
	public void testSyncExceptionallyNoError() throws InterruptedException {
		AtomicReference<Throwable> err = new AtomicReference<>();
		AtomicReference<String> result = new AtomicReference<>();
		target.setSuccess("Hello!");
		future.exceptionally(err::set)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello!"));
		assertThat(err.get(),nullValue());
	}
	
	@Test
	public void testAsyncExceptionallyNoError() throws InterruptedException {
		AtomicReference<Throwable> err = new AtomicReference<>();
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setSuccess("Hello!"), 10, TimeUnit.MILLISECONDS);
		future.exceptionally(err::set)
			.thenAccept(result::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),equalTo("Hello!"));
		assertThat(err.get(),nullValue());
	}
	
	@Test
	public void testAsyncExceptionallyeButExceptionOnHandle() throws InterruptedException {
		AtomicReference<Throwable> err = new AtomicReference<>();
		AtomicReference<String> result = new AtomicReference<>();
		eventloopGroup.schedule(()->target.setFailure(new RuntimeException("Error!")), 10, TimeUnit.MILLISECONDS);
		future.exceptionally(ee->{throw new RuntimeException("New Error!");})
			.thenAccept(result::set)
			.exceptionally(err::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),nullValue());
		assertThat(err.get(),notNullValue());
		assertThat(err.get().getMessage(),equalTo("New Error!"));
	}
	
	@Test
	public void testSyncExceptionallyeButExceptionOnHandle() throws InterruptedException {
		AtomicReference<Throwable> err = new AtomicReference<>();
		AtomicReference<String> result = new AtomicReference<>();
		target.setFailure(new RuntimeException("Error!"));
		future.exceptionally(ee->{throw new RuntimeException("New Error!");})
			.thenAccept(result::set)
			.exceptionally(err::set)
			.await(1,TimeUnit.SECONDS);
		
		assertThat(result.get(),nullValue());
		assertThat(err.get(),notNullValue());
		assertThat(err.get().getMessage(),equalTo("New Error!"));
	}
	
}
