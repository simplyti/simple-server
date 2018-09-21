package com.simplyti.service.api.filter;

import java.util.Collection;
import java.util.Iterator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class FilterChain<T> implements FilterContext<T> {

	private final Promise<Boolean> promise;
	private final Iterator<? extends Filter<T>> iterator;
	private final T msg;
	private final Channel channel;

	private FilterChain(Collection<? extends Filter<T>> filters, ChannelHandlerContext ctx, T msg) {
		this.iterator =  filters.iterator();
		this.channel=ctx.channel();
		this.promise = ctx.executor().newPromise();
		this.msg=msg;
	}

	public static <T> FilterChain<T> of(Collection<? extends Filter<T>> filters, ChannelHandlerContext ctx, T msg) {
		return new FilterChain<>(filters,ctx,msg);
	}

	public Future<Boolean> execute() {
		iterator.next().execute(this);
		return promise;
	}

	@Override
	public void done(boolean handled) {
		if(!handled && iterator.hasNext()) {
			iterator.next().execute(this);
		}else {
			promise.setSuccess(handled);
		}
	}

	@Override
	public void fail(Throwable cause) {
		promise.setFailure(cause);
	}

	@Override
	public T object() {
		return msg;
	}

	@Override
	public Channel channel() {
		return channel;
	}

}
