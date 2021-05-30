package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.pool.ChannelPool;

public class MultiplexedClientChannel extends EmbeddedChannel implements ClientChannel {
	
	private final long streamId;
	private final Address address;
	private final ChannelPool pool;

	public MultiplexedClientChannel(long streamId, Channel parent, ChannelPool pool, Address address) {
		super(parent,null,true,false);
		this.streamId=streamId;
		this.address=address;
		this.pool=pool;
	}

	@Override
	public Address address() {
		return address;
	}

	@Override
	public Future<Void> release() {
		pipeline().fireUserEventTriggered(ClientChannelEvent.RELEASED);
		return new DefaultFuture<>(pool.release(this), eventLoop());
	}
	
	public long streamId() {
		return streamId;
	}
	
}
