package com.simplyti.service.clients;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

@Accessors(fluent=true)
public class ClientChannel<T> implements Channel {

	@Delegate(types=Channel.class)
	private final Channel channel;
	
	@Getter
	private final Promise<T> promise;
	
	private final ChannelPool pool;

	public ClientChannel(ChannelPool pool, Channel channel, Promise<T> promise) {
		this.channel=channel;
		this.promise=promise;
		this.pool=pool;
	}

	public void release() {
		pool.release(channel);
	}


}
