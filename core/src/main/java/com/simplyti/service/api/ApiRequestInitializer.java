package com.simplyti.service.api;

import com.simplyti.service.channel.handler.inits.ChannelHandlerEntry;

public interface ApiRequestInitializer {

	public static final ApiRequestInitializer NONE = () -> null;

	static ApiRequestInitializer none() {
		return NONE;
	}

	ChannelHandlerEntry[] handlers();

}
