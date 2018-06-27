package com.simplyti.service.api.filter;

import io.netty.channel.Channel;

public interface FilterContext<T> {

	public void done();

	public void fail(Throwable unauthorizedException);

	public T object();
	
	public Channel channel();

}
