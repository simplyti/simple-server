package com.simplyti.service.channel.handler.inits;

import io.netty.channel.ChannelHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent=true)
public class ChannelHandlerEntry {
	
	private final String name;
	private final ChannelHandler handler;

}
