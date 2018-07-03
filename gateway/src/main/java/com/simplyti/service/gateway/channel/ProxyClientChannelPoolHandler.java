package com.simplyti.service.gateway.channel;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpClientCodec;

public class ProxyClientChannelPoolHandler extends AbstractChannelPoolHandler {

	@Override
	public void channelCreated(Channel ch) throws Exception {
		ch.pipeline().addLast(new HttpClientCodec());
	}

}
