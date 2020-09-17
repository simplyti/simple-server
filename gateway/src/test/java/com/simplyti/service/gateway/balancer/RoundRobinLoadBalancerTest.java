package com.simplyti.service.gateway.balancer;

import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.HttpEndpoint;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class RoundRobinLoadBalancerTest {
	
	private ServiceBalancer balancer;
	private NioEventLoopGroup eventloopgroup;

	@Before
	public void start() {
		balancer = RoundRobinLoadBalancer.of(Arrays.asList(HttpEndpoint.of("a:8080"),HttpEndpoint.of("b:8080")));
		eventloopgroup = new NioEventLoopGroup(600, new DefaultThreadFactory("test", true));
	}
	
	@Test
	public void testRoundRobin() throws InterruptedException, ExecutionException {
		AtomicInteger countA = new AtomicInteger();
		AtomicInteger countB = new AtomicInteger();
		for(int i=0;i<1000;i++) {
			Endpoint next = balancer.next();
			if(next == null) {
				throw new RuntimeException();
			} else {
				if(next.address().host().equals("a")) countA.incrementAndGet();
				if(next.address().host().equals("b")) countB.incrementAndGet();
			}
		}
		
		assertThat(countA.intValue(),equalTo(500));
		assertThat(countB.intValue(),equalTo(500));
	}

	@Test
	public void concurrentRoundRobin() throws InterruptedException, ExecutionException {
		PromiseCombiner combiner = new PromiseCombiner(ImmediateEventExecutor.INSTANCE);
		EventLoop aggregatorExecutor = eventloopgroup.next();
		AtomicInteger countA = new AtomicInteger();
		AtomicInteger countB = new AtomicInteger();
		for(int i=0;i<1000;i++) {
			Promise<Void> promise = aggregatorExecutor.newPromise();
			combiner.add((Future<?>)promise);
			eventloopgroup.execute(()->{
				Endpoint next = balancer.next();
				if(next == null) {
					promise.setFailure(new NullPointerException());
				} else {
					if(next.address().host().equals("a")) countA.incrementAndGet();
					if(next.address().host().equals("b")) countB.incrementAndGet();
					promise.setSuccess(null);
				}
			});
		}
		Promise<Void> aggregatePromise = aggregatorExecutor.newPromise();
		combiner.finish(aggregatePromise);
		aggregatePromise.sync();
		
		assertThat(Math.abs(((double)countA.intValue()/1000)-0.5),lessThan(0.1));
		assertThat(Math.abs(((double)countB.intValue()/1000)-0.5),lessThan(0.1));
	}

}
