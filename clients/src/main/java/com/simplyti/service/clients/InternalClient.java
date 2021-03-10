package com.simplyti.service.clients;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.channel.SimpleChannelPoolMap;
import com.simplyti.service.clients.channel.monitor.ClientMonitor;
import com.simplyti.service.clients.channel.monitor.ClientMonitorHandler;
import com.simplyti.service.clients.channel.monitor.MonitoredHandler;
import com.simplyti.service.clients.init.ClientRequestChannelInitializer;
import com.simplyti.service.clients.trace.RequestTracerHandler;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.util.concurrent.DefaultFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

public class InternalClient implements ClientMonitor, ClientMonitorHandler, ChannelHealthChecker {
	
	private static final AttributeKey<Instant> LAST_USAGE = AttributeKey.newInstance("last.usage");

	private final EventLoopGroup eventLoopGroup;
	private final SimpleChannelPoolMap channelPoolMap;
	
	private final ChannelGroup allChannels;
	private final ChannelGroup activeChannels;
	private final ChannelGroup iddleChannels;

	private PoolConfig poolConfig;
	
	public InternalClient(EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, ChannelFactory<Channel> channelFactory, PoolConfig poolConfig) {
		this(null,eventLoopGroup,poolHandler,channelFactory, poolConfig);
	}
	
	public InternalClient(SslProvider sslProvider, EventLoopGroup eventLoopGroup, ChannelPoolHandler poolHandler, ChannelFactory<Channel> channelFactory,PoolConfig poolConfig) {
		this.eventLoopGroup = eventLoopGroup;
		EventLoop channelGroupsEventLoop = eventLoopGroup.next();
		this.allChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.activeChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.iddleChannels=new DefaultChannelGroup(channelGroupsEventLoop);
		this.channelPoolMap = new SimpleChannelPoolMap(sslProvider,eventLoopGroup, new MonitoredHandler(this, poolHandler), this, channelFactory, poolConfig);
		this.poolConfig = poolConfig;
	}
	
	public ChannelPool pool(Endpoint endpoint) {
		return channelPoolMap.get(endpoint);
	}
	
	public void released(Channel ch) {
		ch.attr(LAST_USAGE).set(Instant.now());
		activeChannels.remove(ch);
		iddleChannels.add(ch);
	}

	public void acquired(Channel ch) {
		iddleChannels.remove(ch);
		activeChannels.add(ch);
	}
	
	@Override
	public Future<Boolean> isHealthy(Channel ch) {
		EventLoop loop = ch.eventLoop();
		if(!ch.isActive()) {
			return loop.newSucceededFuture(Boolean.FALSE);
		}
		
		final Instant lastUsage = ch.attr(LAST_USAGE).getAndSet(Instant.now());
		if(poolConfig==null || poolConfig.maxIdle() <0 || lastUsage == null) {
			return loop.newSucceededFuture(Boolean.TRUE);
		}
		
		long iddleTime = ChronoUnit.SECONDS.between(lastUsage, Instant.now());
		if(iddleTime>this.poolConfig.maxIdle() ) {
			ch.close();
			return loop.newSucceededFuture(Boolean.FALSE);
		}
		
		return loop.newSucceededFuture(Boolean.TRUE);
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

	public <T> com.simplyti.util.concurrent.Future<T> channel(ClientRequestChannelInitializer<T> initializer, Object message, ClientConfig config) {
		EventLoop eventLoop = eventLoopGroup.next();
		Promise<T> resultPromise = eventLoop.newPromise();
		return channel(initializer, message,config,eventLoop,resultPromise);
	}

	public <T> com.simplyti.util.concurrent.Future<T> channel(ClientRequestChannelInitializer<T> initializer, Object message, ClientConfig config, EventLoop eventLoop, Promise<T> resultPromise) {
		Future<ClientRequestChannel<T>> clientFuture = this.channel(config,initializer, resultPromise);
		DefaultFuture<T> future = new DefaultFuture<>(resultPromise,eventLoop);
		if(clientFuture.isDone()) {
			if(clientFuture.isSuccess()) {
				clientFuture.getNow().writeAndFlush(message).addListener(write->handleWriteFuture(clientFuture.getNow(),write,config.timeoutMillis()));
			}else {
				resultPromise.setFailure(clientFuture.cause());
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
		return future;
	}

	public <T> void handleWriteFuture(ClientRequestChannel<?> channel, Future<?> future, long timeoutMillis) {
		if (!future.isSuccess()) {
			if(channel.eventLoop().inEventLoop()) {
				channel.setFailure(future.cause());
			}else {
				channel.eventLoop().submit(()->channel.setFailure(future.cause()));
			}
        }else if(timeoutMillis>0) {
        		ScheduledFuture<?> timeoutTask = channel.eventLoop().schedule(() -> readTimeOutFail(channel), timeoutMillis, TimeUnit.MILLISECONDS);
        		channel.addListener(ignore -> timeoutTask.cancel(false));
        }
	}

	private void readTimeOutFail(ClientRequestChannel<?> channel) {
		channel.setFailure(ReadTimeoutException.INSTANCE);
		for(Entry<String, ChannelHandler> handler:channel.pipeline()){
			channel.pipeline().remove(handler.getValue());
		}
		channel.close();
	}

	public <T> Future<ClientRequestChannel<T>> channel(ClientConfig config, ClientRequestChannelInitializer<T> clientRequestChannelHandler, Promise<T> resultPromise) {
		ChannelPool pool = channelPoolMap.get(config.endpoint());
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
					Promises.toPromise(fcrc,clientPromise);
				} else {
					clientPromise.setFailure(channelFuture.cause());
				}
			});
			return clientPromise;
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

	public Future<Void> closeIdleClannels() {
		return iddleChannels.close();
	}

}
