package com.simplyti.service.clients;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

@Accessors(fluent=true)
public class ClientRequestChannel<T> implements Channel {

	@Delegate(types=Channel.class)
	private final Channel channel;
	
	private final Promise<T> resultPromise;
	private final ChannelPool pool;

	public ClientRequestChannel(ChannelPool pool, Channel channel, Promise<T> resultPromise) {
		this.channel=channel;
		this.resultPromise=resultPromise;
		this.pool=pool;
	}

	public void release() {
		this.channel.pipeline().fireUserEventTriggered(ClientChannelEvent.END_REQUEST);
		pool.release(channel);
	}

	public void setFailure(Throwable cause) {
		if(!resultPromise.isDone()) {
			this.resultPromise.setFailure(cause);
		}
	}

	public boolean isDone() {
		return resultPromise.isDone();
	}

	public boolean isSuccess() {
		return resultPromise.isSuccess();
	}

	public void setSuccess(T result) {
		this.resultPromise.setSuccess(result);
	}

	public Promise<T> addListener(GenericFutureListener<? extends Future<? super T>> listener) {
		return resultPromise.addListener(listener);
	}

	public Throwable cause() {
		return resultPromise.cause();
	}
	
	public Promise<T> resultPromise(){
		return resultPromise;
	}

}
