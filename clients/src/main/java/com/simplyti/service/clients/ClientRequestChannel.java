package com.simplyti.service.clients;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

@Accessors(fluent=true)
public class ClientRequestChannel<T> implements Channel {

	@Delegate(types=Channel.class)
	private final Channel channel;
	
	@Getter
	private final Promise<T> resultPromise;
	
	private final ChannelPool pool;

	public ClientRequestChannel(ChannelPool pool, Channel channel, Promise<T> resultPromise) {
		this.channel=channel;
		this.resultPromise=resultPromise;
		this.pool=pool;
	}

	public void release() {
		pool.release(channel);
	}

}
