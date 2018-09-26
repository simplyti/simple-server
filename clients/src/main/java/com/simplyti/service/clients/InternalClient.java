package com.simplyti.service.clients;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.channel.SimpleChannelPoolMap;
import com.simplyti.service.clients.channel.monitor.ClientMonitor;
import com.simplyti.service.clients.channel.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.channel.monitor.MonitoredHandler;
import com.simplyti.service.clients.init.ClientRequestChannelInitializer;
import com.simplyti.service.clients.trace.RequestTracerHandler;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.timeout.ReadTimeoutException;
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

	public <T> Future<T> channel(Endpoint endpoint, ClientRequestChannelInitializer<T> initializer, Object message, ClientConfig config) {
		EventLoop eventLoop = eventLoopGroup.next();
		Promise<T> resultPromise = eventLoop.newPromise();
		
		Future<ClientRequestChannel<T>> clientFuture = this.channel(config,endpoint,initializer, resultPromise);
		if(clientFuture.isDone()) {
			if(clientFuture.isSuccess()) {
				clientFuture.getNow().writeAndFlush(message).addListener(write->handleWriteFuture(clientFuture.getNow(),write,config.timeoutMillis()));
			}else {
				return resultPromise.setFailure(clientFuture.cause());
			}
		}else {
			clientFuture.addListener(f->{
				if(clientFuture.isSuccess()) {
					clientFuture.getNow().writeAndFlush(message).addListener(write->handleWriteFuture(clientFuture.getNow(),write,config.timeoutMillis()));
				}else {
					resultPromise.setFailure(clientFuture.cause());
				}
			});
		}
		return resultPromise;
	}

	public void handleWriteFuture(ClientRequestChannel<?> channel, Future<?> future, long timeoutMillis) {
		if (!future.isSuccess()) {
			channel.pipeline().fireExceptionCaught(future.cause());
        }else if(timeoutMillis>0) {
        		ScheduledFuture<?> timeoutTask = channel.eventLoop().schedule(() -> channel.setFailure(ReadTimeoutException.INSTANCE), timeoutMillis, TimeUnit.MILLISECONDS);
        		channel.addListener(ignore -> timeoutTask.cancel(false));
        }
	}

	public <T> Future<ClientRequestChannel<T>> channel(ClientConfig config, Endpoint endpoint, ClientRequestChannelInitializer<T> clientRequestChannelHandler, Promise<T> resultPromise) {
		ChannelPool pool = channelPoolMap.get(endpoint);
		Future<Channel> channelFuture = pool.acquire();
		if (channelFuture.isDone()) {
			if (channelFuture.isSuccess()) {
				Channel channel = channelFuture.getNow();
				return clientRequestChannel(config,clientRequestChannelHandler, pool,channel,resultPromise);
			} else {
				return eventLoopGroup.next().newFailedFuture(channelFuture.cause());
			}
		} else {
			EventLoop eventLoop = eventLoopGroup.next();
			Promise<ClientRequestChannel<T>> clientPromise = eventLoop.newPromise();
			channelFuture.addListener(f -> {
				if (channelFuture.isSuccess()) {
					Future<ClientRequestChannel<T>> fcrc = clientRequestChannel(config,clientRequestChannelHandler, pool,channelFuture.getNow(),resultPromise);
					fcrc.addListener(f2->toPromise(fcrc,clientPromise));
				} else {
					clientPromise.setFailure(channelFuture.cause());
				}
			});
			return clientPromise;
		} 
	}

	private <T> void toPromise(Future<ClientRequestChannel<T>> future, Promise<ClientRequestChannel<T>> promise) {
		if(future.isSuccess()) {
			promise.setSuccess(future.getNow());
		}else {
			promise.setFailure(future.cause());
		}
	}

	private <T> Future<ClientRequestChannel<T>> clientRequestChannel(ClientConfig config, ClientRequestChannelInitializer<T> clientRequestChannelHandler, ChannelPool pool, Channel channel, Promise<T> resultPromise) {
		ClientRequestChannel<T> clientRequestChannel = new ClientRequestChannel<>(pool,channel,resultPromise);
		clientRequestChannelHandler.initialize(clientRequestChannel);
		addTracer(clientRequestChannel,config);
		EventLoop channelLoop = channel.eventLoop();
		if(ChannelClientInitHandler.isInitialized(channel)) {
			return channelLoop.newSucceededFuture(clientRequestChannel);
		}else {
			Promise<ClientRequestChannel<T>> clientPromise =  channelLoop.newPromise();
			if(channelLoop.inEventLoop()) {
				initialize(clientRequestChannel,clientPromise);
			}else {
				channelLoop.execute(()->initialize(clientRequestChannel,clientPromise));
			}
			return clientPromise;
		}
	}

	private void addTracer(Channel channel, ClientConfig config) {
		if(config.tracer()!=null) {
			channel.pipeline().addLast(new RequestTracerHandler<>(config.tracer()));
		}
	}

	private <T> void initialize(ClientRequestChannel<T> clientRequestChannel, Promise<ClientRequestChannel<T>> clientPromise) {
		clientRequestChannel.pipeline().addLast(new ChannelClientInitHandler<>(clientPromise,clientRequestChannel));
		clientRequestChannel.pipeline().fireUserEventTriggered(ClientChannelEvent.INIT);
	}

	public EventLoopGroup eventLoopGroup() {
		return eventLoopGroup;
	}

	public Future<Void> close() {
		return allChannels.close();
	}

}
