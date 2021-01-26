package com.simplyti.service.clients.http;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.http.handler.HttpClientFullResponseAggregator;
import com.simplyti.service.clients.http.handler.HttpContentUnwrapHandled;
import com.simplyti.service.clients.http.handler.SetHostHeaderHandler;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class HttpClientChannelPoolHandler extends AbstractChannelPoolHandler {
	
	private final SetHostHeaderHandler setHostHeaderHandler;
	private final long readTimeoutMillis;

	public HttpClientChannelPoolHandler(long readTimeoutMillis) {
		this.setHostHeaderHandler = new SetHostHeaderHandler();
		this.readTimeoutMillis=readTimeoutMillis;
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		if(readTimeoutMillis >0) {
			ch.pipeline().addLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS));
		}
		ch.pipeline().addLast(new HttpClientCodec());
		ch.pipeline().addLast(new HttpClientFullResponseAggregator(52428800));
		ch.pipeline().addLast(new HttpContentUnwrapHandled());
		ch.pipeline().addLast(setHostHeaderHandler);
	}
	
}
