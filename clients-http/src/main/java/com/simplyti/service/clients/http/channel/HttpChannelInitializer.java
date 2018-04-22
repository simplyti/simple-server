package com.simplyti.service.clients.http.channel;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpClientCodec;

public class HttpChannelInitializer extends AbstractChannelPoolHandler{
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		ch.pipeline().addLast(new HttpClientCodec());
	}
}
