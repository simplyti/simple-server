package com.simplyti.server.http.api.handler;

import com.simplyti.service.channel.handler.inits.ChannelHandlerEntry;

public interface ApiRequestInitializer {

	ChannelHandlerEntry[] handlers();

}
