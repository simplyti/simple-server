package com.simplyti.service.clients;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.channel.SimpleChannelPoolMap;
import com.simplyti.service.clients.channel.monitor.ClientMonitor;
import com.simplyti.service.clients.channel.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.channel.monitor.MonitoredHandler;
import com.simplyti.service.clients.init.ClientRequestChannelInitializer;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.vavr.control.Try;

public class InternalClient implements ClientMonitor, ClientMonitorHandler {

	private static final AttributeKey<Boolean> INITIALIZED = AttributeKey.valueOf("client.initialized");
	
	private final EventLoopGroup eventLoopGroup;
	private final SimpleChannelPoolMap channelPoolMap;
	
	private final ChannelGroup allChannels;
	private final ChannelGroup activeChannels;
	private final ChannelGroup iddleChannels;


	public InternalClient(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler) {
		this.eventLoopGroup = eventLoopGroup;
		EventLoop channelGroupsEventLoop = eventLoopGroup.next();
		this.allChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.activeChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.iddleChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.channelPoolMap = new SimpleChannelPoolMap(eventLoopGroup, new MonitoredHandler(this, poolHandler));
	}
	
	public ChannelPool pool(Endpoint endpoint) {
		return channelPoolMap.get(endpoint);
	}
	
	public void released(Channel ch) {
		activeChannels.remove(ch);
		iddleChannels.add(ch);
	}

	public void acquired(Channel ch) {
		iddleChannels.remove(ch);
		activeChannels.add(ch);
	}

	public void created(Channel ch) {
		allChannels.add(ch);
		activeChannels.add(ch);
	}

	public int iddleConnections() {
		return iddleChannels.size();
	}

	public int activeConnections() {
		return activeChannels.size();
	}

	public int totalConnections() {
		return allChannels.size();
	}

	
	public <T> Future<T> channel(Endpoint endpoint, ClientRequestChannelInitializer<T> initializer, Object message, long timeoutMillis) {
		Promise<T> promise = eventLoopGroup.next().newPromise();
		Future<ClientRequestChannel<T>> clientFuture = this.channel(endpoint,initializer, promise);
		if(clientFuture.isDone()) {
			if(clientFuture.isSuccess()) {
				clientFuture.getNow().writeAndFlush(message).addListener(write->handleWriteFuture(clientFuture.getNow(),write,timeoutMillis));
			}else {
				return promise.setFailure(clientFuture.cause());
			}
		}else {
			clientFuture.addListener(f->{
				if(clientFuture.isSuccess()) {
					clientFuture.getNow().writeAndFlush(message).addListener(write->handleWriteFuture(clientFuture.getNow(),write,timeoutMillis));
				}else {
					promise.setFailure(clientFuture.cause());
				}
			});
		}
		return promise;
	}

	public void handleWriteFuture(ClientRequestChannel<?> channel, Future<?> future, long timeoutMillis) {
		if (!future.isSuccess()) {
			channel.pipeline().fireExceptionCaught(future.cause());
        }else if(timeoutMillis>0) {
        		ScheduledFuture<?> timeoutTask = channel.eventLoop().schedule(() -> channel.resultPromise().setFailure(ReadTimeoutException.INSTANCE), timeoutMillis, TimeUnit.MILLISECONDS);
        		channel.resultPromise().addListener(ignore -> timeoutTask.cancel(false));
        }
	}

	public <T> Future<ClientRequestChannel<T>> channel(Endpoint endpoint, ClientRequestChannelInitializer<T> requestChannelInitializer, Promise<T> promise) {
		ChannelPool pool = channelPoolMap.get(endpoint);
		Future<Channel> channelFuture = pool.acquire();
		if (channelFuture.isDone()) {
			if (channelFuture.isSuccess()) {
				EventLoop eventLoop = channelFuture.getNow().eventLoop();
				ClientRequestChannel<T> client = new ClientRequestChannel<>(pool,channelFuture.getNow(),promise);
				Try.run(()->channelFuture.getNow().pipeline().remove(ChannelClientInitHandler.class));
				requestChannelInitializer.initialize(client);
				client.pipeline().addLast(new ChannelClientInitHandler<T>(null,client));
				return eventLoop.newSucceededFuture(client);
			} else {
				EventLoop eventLoop = eventLoopGroup.next();
				return eventLoop.newFailedFuture(channelFuture.cause());
			}
		} else {
			EventLoop eventLoop = eventLoopGroup.next();
			Promise<ClientRequestChannel<T>> clientPromise = eventLoop.newPromise();
			channelFuture.addListener(f -> {
				if (channelFuture.isSuccess()) {
					Channel channel = channelFuture.getNow();
					ClientRequestChannel<T> clientChannel = new ClientRequestChannel<>(pool,channel,promise);
					Try.run(()->channelFuture.getNow().pipeline().remove(ChannelClientInitHandler.class));
					requestChannelInitializer.initialize(clientChannel);
					if(channel.attr(INITIALIZED).get()==null) {
						clientChannel.pipeline().addLast(new ChannelClientInitHandler<>(clientPromise,clientChannel));
						clientChannel.pipeline().fireUserEventTriggered(ClientChannelEvent.INIT);
						channel.attr(INITIALIZED).set(true);
					}else {
						clientChannel.pipeline().addLast(new ChannelClientInitHandler<T>(null,clientChannel));
						clientPromise.setSuccess(clientChannel);
					}
				} else {
					clientPromise.setFailure(channelFuture.cause());
				}
			});
			return clientPromise;
		} 
	}

	public EventLoopGroup eventLoopGroup() {
		return eventLoopGroup;
	}

}
