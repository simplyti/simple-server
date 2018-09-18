package com.simplyti.service.api.filter;

import io.netty.channel.Channel;

public interface FilterContext<T> {
	
	public default void done() {
		done(false);
	}

	public void done(boolean doContinue);

	public void fail(Throwable unauthorizedException);

	public T object();
	
	public Channel channel();

}
