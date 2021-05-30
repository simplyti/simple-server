package com.simplyti.service.clients.channel;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.simplyti.service.clients.endpoint.Address;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PlatformDependent;

public class SimpleMultiplexedChannelPool implements ChannelPool {
	
	private static final AttributeKey<AtomicLong> ID_GENERATOR_ATT = AttributeKey.newInstance("com.simplyti.service.clients.channel.com.simplyti.service.clients");

	private final Bootstrap bootstrap;
	private final ChannelPoolHandler handler;
	private final Map<Channel,Deque<MultiplexedClientChannel>> physicals;
	private final boolean lastRecentUsed;
	private final Address address;

	private final Deque<Channel> deque;
	

	public SimpleMultiplexedChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler, Address address) {
		this.bootstrap=bootstrap.clone().handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                handler.channelCreated(ch);
            }
        });
		this.handler=handler;
		this.lastRecentUsed=true;
		this.deque = PlatformDependent.newConcurrentDeque();
		this.physicals = PlatformDependent.newConcurrentHashMap();
		this.address=address;
	}

	@Override
	public Future<Channel> acquire() {
		return acquire(bootstrap.config().group().next().newPromise());
	}

	@Override
	public Future<Channel> acquire(Promise<Channel> promise) {
		return acquireHealthyFromPoolOrNew(promise);
	}
	
	private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
		 final Channel ch = pollChannel();
		 if(ch !=null) {
			 Deque<MultiplexedClientChannel> logicalDeque = physicals.get(ch);
			 MultiplexedClientChannel mch = poolLogicalChannel(logicalDeque);
			 if(mch!=null) {
				 promise.setSuccess(mch);
				 return promise;
			 }
			 return newLogicalChannel(ch,promise);
		 }
		 
		 return newPhysicalChannel(promise);
	}
	
	private Future<Channel> newPhysicalChannel(Promise<Channel> promise) {
		ChannelFuture cf = bootstrap.connect();
		 if (cf.isDone()) {
			 handleConnect(cf, promise);
		 } else {
			 cf.addListener(f->handleConnect(cf,promise));
		 }
		 return promise;
	}

	private void handleConnect(ChannelFuture future, Promise<Channel> promise) {
		 if (future.isSuccess()) {
	            Channel channel = future.channel();
	            handleConnect(channel,promise);
	        } else {
	            promise.tryFailure(future.cause());
	        }
	}
	
	private Future<Channel> newLogicalChannel(Channel channel, Promise<Channel> promise) {
		AtomicLong atomicLong = channel.attr(ID_GENERATOR_ATT).get();
		MultiplexedClientChannel clientChannel = new MultiplexedClientChannel(atomicLong.getAndIncrement(), channel, this, address);
		clientChannel.pipeline().addLast(new MultiplexChannelToParentHandler(channel));
		channel.pipeline().get(MultiplexStreamlHandler.class).newStream(clientChannel);
		try {
        	handler.channelAcquired(clientChannel);
        	if (!promise.trySuccess(clientChannel)) {
        		clientChannel.release();
            }
        } catch (Throwable cause) {
            promise.tryFailure(cause);
            clientChannel.release();
        }
		return promise;
	}
	
	private void handleConnect(Channel channel, Promise<Channel> promise) {
		AtomicLong atomicLong = new AtomicLong();
		MultiplexedClientChannel clientChannel = new MultiplexedClientChannel(atomicLong.getAndIncrement(), channel, this, address);
		channel.attr(ID_GENERATOR_ATT).set(atomicLong);
		clientChannel.pipeline().addLast(new MultiplexChannelToParentHandler(channel));
		channel.pipeline().addLast(new MultiplexStreamlHandler(clientChannel));
		physicals.put(channel, PlatformDependent.newConcurrentDeque());
		deque.offer(channel);
		try {
        	handler.channelAcquired(clientChannel);
        	if (!promise.trySuccess(clientChannel)) {
        		clientChannel.release();
            }
        } catch (Throwable cause) {
            promise.tryFailure(cause);
            clientChannel.release();
        }
	}

	protected Channel pollChannel() {
        return lastRecentUsed ? deque.peekLast() : deque.peekFirst();
    }
	
	private MultiplexedClientChannel poolLogicalChannel(Deque<MultiplexedClientChannel> logicalDeque) {
		return lastRecentUsed ? logicalDeque.pollLast() : logicalDeque.pollFirst();
	}

	@Override
	public Future<Void> release(Channel channel) {
		return release(channel, channel.eventLoop().newPromise());
	}

	@Override
	public Future<Void> release(Channel channel, Promise<Void> promise) {
		EventLoop loop = channel.eventLoop();
        if (loop.inEventLoop()) {
        	release0((MultiplexedClientChannel) channel, promise);
        } else {
        	loop.execute(()->release0((MultiplexedClientChannel) channel, promise));
        }
        return promise;
	}

	private void release0(MultiplexedClientChannel channel, Promise<Void> promise) {
		try{
			handler.channelReleased(channel);
			physicals.get(channel.parent()).offer(channel);
		} catch(Throwable cause) {
			channel.close();
			 promise.tryFailure(cause);
		}
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

}
