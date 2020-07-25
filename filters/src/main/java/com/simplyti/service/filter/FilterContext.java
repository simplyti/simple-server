package com.simplyti.service.filter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface FilterContext<T> {
	
	public default void done() {
		done(false);
	}

	public void done(boolean breakFilter);

	public void fail(Throwable cause);

	public T object();
	
	public Channel channel();

	public ChannelHandlerContext channelContext();

}
