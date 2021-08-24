package com.simplyti.service;

import java.util.Collections;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.awaitility.Awaitility.await;

import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.hook.ServerStartHook;
import com.simplyti.service.hook.ServerStopHook;
import com.simplyti.service.matcher.di.InstanceProvider;
import com.simplyti.service.transport.ServerTransport;
import com.simplyti.util.concurrent.DefaultFuture;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;

public class DefaultServerTest {
	
	private NioEventLoopGroup eventLoopGroup;
	private EventLoop startStopLoop;
	private DefaultServerStopAdvisor serverStopAdvisor;
	private ClientChannelGroup clientChannelGroup;
	private ServerTransport transport;
	private InstanceProvider instanceProvider;
	
	private Server service;

	@Before
	public void prepare() {
		this.eventLoopGroup = new NioEventLoopGroup(); 
		this.startStopLoop = new NioEventLoopGroup(1).next();
		this.serverStopAdvisor = new DefaultServerStopAdvisor();
		this.clientChannelGroup = new ClientChannelGroup(startStopLoop);
		this.transport = mock(ServerTransport.class);
		this.instanceProvider = mock(InstanceProvider.class);
	}
	
	@After
	public void teardown() {
		this.eventLoopGroup.shutdownGracefully();
		this.startStopLoop.shutdownGracefully();
	}
	
	@Test
	public void startAndStopTest() throws InterruptedException {
		when(transport.start(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		when(transport.stop(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		newServer(Collections.emptySet(),Collections.emptySet());
		Future<Server> futureStart = service.start().await();
		assertTrue(futureStart.isDone());
		assertTrue(futureStart.isSuccess());
		
		Future<Server> repeatStartFuture = this.service.start().await();
		assertTrue(repeatStartFuture.isDone());
		assertTrue(repeatStartFuture.isSuccess());
		assertThat(futureStart.getNow(),equalTo(repeatStartFuture.getNow()));
		
		Future<Void> futureStop = service.stop();
		Future<Void> repeatedStop = service.stop().await();
		
		assertTrue(futureStop.isDone());
		assertTrue(futureStop.isSuccess());
		assertTrue(repeatedStop.isDone());
		assertTrue(repeatedStop.isSuccess());
		await().until(startStopLoop::isShutdown);
		await().until(startStopLoop::isShutdown);
	}
	
	@Test
	public void startErrorTest() throws InterruptedException {
		when(transport.start(any())).thenReturn(new DefaultFuture<>(startStopLoop.newFailedFuture(new RuntimeException("Transport error")),startStopLoop));
		when(transport.stop(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		newServer(Collections.emptySet(),Collections.emptySet());
		Future<Server> futureStart = service.start().await();
		assertTrue(futureStart.isDone());
		assertFalse(futureStart.isSuccess());
		assertThat(futureStart.cause().getMessage(),equalTo("Transport error"));
		await().until(startStopLoop::isShutdown);
		await().until(startStopLoop::isShutdown);
	}
	
	@Test
	public void startHooksTest() throws InterruptedException {
		when(transport.start(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		when(transport.stop(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		ServerStartHook startHook = mock(ServerStartHook.class);
		when(startHook.executeStart(any())).thenReturn(startStopLoop.newSucceededFuture(null));
		newServer(Collections.singleton(startHook),Collections.emptySet());
		Future<Server> futureStart = service.start().await();
		assertTrue(futureStart.isDone());
		assertTrue(futureStart.isSuccess());
	}
	
	@Test
	public void startHookErrorTest() throws InterruptedException {
		when(transport.start(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		when(transport.stop(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		ServerStartHook startHook = mock(ServerStartHook.class);
		when(startHook.executeStart(any())).thenReturn(startStopLoop.newFailedFuture(new RuntimeException("Transport error")));
		newServer(Collections.singleton(startHook),Collections.emptySet());
		Future<Server> futureStart = service.start().await();
		assertTrue(futureStart.isDone());
		assertFalse(futureStart.isSuccess());
		assertThat(futureStart.cause().getMessage(),equalTo("Transport error"));
		await().until(startStopLoop::isShutdown);
		await().until(startStopLoop::isShutdown);
	}
	
	@Test
	public void stopHooksTest() throws InterruptedException {
		when(transport.start(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		when(transport.stop(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		ServerStopHook stoptHook = mock(ServerStopHook.class);
		when(stoptHook.executeStop(any())).thenReturn(startStopLoop.newSucceededFuture(null));
		newServer(Collections.emptySet(),Collections.singleton(stoptHook));
		Future<Server> futureStart = service.start().await();
		assertTrue(futureStart.isDone());
		assertTrue(futureStart.isSuccess());
	}
	
	@Test
	public void stopWithConnectedClientsTest() throws InterruptedException {
		when(transport.start(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		when(transport.stop(any())).thenReturn(new DefaultFuture<>(startStopLoop.newSucceededFuture(null),startStopLoop));
		newServer(Collections.emptySet(),Collections.emptySet());
		Future<Server> futureStart = service.start().await();
		assertTrue(futureStart.isDone());
		assertTrue(futureStart.isSuccess());
		
		Channel clientChannel = new EmbeddedChannel();
		clientChannelGroup.add(clientChannel);
		
		Future<Void> futureStop = service.stop().await();
		assertTrue(futureStop.isDone());
		assertTrue(futureStop.isSuccess());
		await().until(startStopLoop::isShutdown);
		await().until(startStopLoop::isShutdown);
		assertFalse(clientChannel.isActive());
	}

	private void newServer(Set<ServerStartHook> startHooks,Set<ServerStopHook> stopHooks) {
		ServerConfig config = new ServerConfig("test",10,Collections.emptyList(),false,false,100);
		this.service = new DefaultServer(()->eventLoopGroup, serverStopAdvisor, ()->startStopLoop, ()->clientChannelGroup, startHooks, stopHooks, config, Collections.singleton(transport), instanceProvider);
	}

}
