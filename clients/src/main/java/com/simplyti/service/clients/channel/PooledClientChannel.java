package com.simplyti.service.clients.channel;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.experimental.Delegate;

public class PooledClientChannel implements ClientChannel {
	
	@Delegate
	private final Channel channel;
	
	private final Address address;
	private final ChannelPool pool;

	private final ScheduledFuture<?> releaseTimeoutSchedule;

	public PooledClientChannel(ChannelPool pool, Address address, Channel channel, long responseTimeoutMillis) {
		this.pool=pool;
		this.address=address;
		this.channel=channel;
		this.channel.attr(ADDRESS).set(address);
		if(responseTimeoutMillis>0) {
			this.releaseTimeoutSchedule = this.channel.eventLoop().schedule(this::throwResponseTimeout, responseTimeoutMillis, TimeUnit.MILLISECONDS);
		} else {
			this.releaseTimeoutSchedule = null; 
		}
	}
	
	private void throwResponseTimeout() {
		this.channel.pipeline().fireExceptionCaught(ReadTimeoutException.INSTANCE);
	}

	@Override
	public Future<Void> release() {
		if(this.releaseTimeoutSchedule!=null) {
			this.releaseTimeoutSchedule.cancel(false);
		}
		pipeline().fireUserEventTriggered(ClientChannelEvent.RELEASED);
		return new DefaultFuture<>(pool.release(channel),channel.eventLoop());
	}

	@Override
	public Address address() {
		return address;
	}

}
