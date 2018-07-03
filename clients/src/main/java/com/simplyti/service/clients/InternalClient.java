package com.simplyti.service.clients;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.SimpleChannelPoolMap;
import com.simplyti.service.clients.channel.monitor.ClientMonitor;
import com.simplyti.service.clients.channel.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.channel.monitor.MonitoredHandler;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

public class InternalClient implements ClientMonitor, ClientMonitorHandler {

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
	
	public <T> ClientResponseFuture<T> channel(Endpoint endpoint, Object msg, Consumer<ClientChannel<T>> consumer, long timeoutMillis) {
		ChannelPool pool = channelPoolMap.get(endpoint);
		Future<Channel> channelFuture = pool.acquire();
		if (channelFuture.isDone()) {
			if (channelFuture.isSuccess()) {
				EventLoop eventLoop = channelFuture.getNow().eventLoop();
				Promise<T> promise = eventLoop.newPromise();
				send(consumer, pool, channelFuture.getNow(), promise, msg, timeoutMillis);
				return new ClientResponseFuture<>(eventLoop,channelFuture,promise);
			} else {
				ReferenceCountUtil.release(msg);
				EventLoop eventLoop = eventLoopGroup.next();
				Future<T> promise = eventLoop.newFailedFuture(channelFuture.cause());
				return new ClientResponseFuture<>(eventLoop,channelFuture,promise);
			}
		} else {
			EventLoop eventLoop = eventLoopGroup.next();
			Promise<T> promise = eventLoop.newPromise();
			Promise<Channel> initializerChannelFuture = eventLoop.newPromise();
			channelFuture.addListener(f -> {
				if (channelFuture.isSuccess()) {
					send(consumer, pool, channelFuture.getNow(), promise, msg, timeoutMillis);
					initializerChannelFuture.setSuccess(channelFuture.getNow());
				} else {
					ReferenceCountUtil.release(msg);
					promise.setFailure(channelFuture.cause());
					initializerChannelFuture.setFailure(channelFuture.cause());
				}
			});
			return new ClientResponseFuture<>(eventLoop,initializerChannelFuture,promise);
		}
	}

	private <T> void send(Consumer<ClientChannel<T>> channelInit, ChannelPool pool, Channel channel, Promise<T> promise,
			Object msg, long timeoutMillis) {
		channel.closeFuture().addListener(f->promise.tryFailure(new ClosedChannelException()));
		channelInit.accept(new ClientChannel<T>(pool, channel, promise));
		
		channel.writeAndFlush(msg).addListener(f->{
			if (!f.isSuccess()) {
				promise.tryFailure(f.cause());
            }else if(timeoutMillis>0) {
        			ScheduledFuture<?> timeoutTask = channel.eventLoop().schedule(
        					() -> promise.tryFailure(ReadTimeoutException.INSTANCE), timeoutMillis, TimeUnit.MILLISECONDS);
        			promise.addListener(ignore -> timeoutTask.cause());
            }
		});
		
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

	public <T> Promise<T> newPromise() {
		return eventLoopGroup.next().newPromise();
	}

}
