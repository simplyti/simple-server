package com.simplyti.service.clients.http.handler;

import io.netty.channel.ChannelHandler;

public interface ClientChannelInitializerHandler {

	ClientChannelInitializerHandler addLast(ChannelHandler handler);

}
