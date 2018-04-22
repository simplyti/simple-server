package com.simplyti.service.client;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ClientChannelInitializer extends AbstractChannelPoolHandler{
	
	private final ChannelGroup channelGroup;
	private final ServerCertificateHandler serverCertificateHandler;

	public ClientChannelInitializer(ChannelGroup channelGroup,ServerCertificateHandler serverCertificateHandler) {
		this.channelGroup = channelGroup;
		this.serverCertificateHandler=serverCertificateHandler;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
		channelGroup.add(ch);
		ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
		ch.pipeline().addLast(new HttpRequestEncoder());
		ch.pipeline().addLast(new HttpResponseDecoder());
		ch.pipeline().addLast(new HttpObjectAggregator(5242880));
		ch.pipeline().addLast(new ChunkedWriteHandler());
		ch.pipeline().addLast(serverCertificateHandler);
		
	}
}
