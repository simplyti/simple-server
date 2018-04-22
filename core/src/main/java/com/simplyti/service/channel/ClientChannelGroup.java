package com.simplyti.service.channel;

import javax.inject.Inject;

import com.simplyti.service.builder.di.StartStopLoop;

import io.netty.channel.EventLoop;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;

public class ClientChannelGroup extends DefaultChannelGroup{
	
	public static final AttributeKey<Boolean> IN_PROGRESS = AttributeKey.valueOf("client.request.in.progess");
	private static final ChannelMatcher IDDLE_CHANNELS = channel->!Boolean.TRUE.equals(channel.attr(IN_PROGRESS).get());

	@Inject
	public ClientChannelGroup(@StartStopLoop EventLoop eventLoop) {
		super(eventLoop);
	}

	public ChannelGroupFuture closeIddleChannels() {
		return this.close(IDDLE_CHANNELS);
	}


}
