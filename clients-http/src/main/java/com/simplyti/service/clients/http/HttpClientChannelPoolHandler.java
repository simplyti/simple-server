package com.simplyti.service.clients.http;

import java.util.List;
import java.util.stream.Collectors;

import com.simplyti.service.clients.channel.pool.AbstractClientPoolHandler;
import com.simplyti.service.clients.http.handler.HttpClientFullResponseAggregator;
import com.simplyti.service.clients.http.handler.HttpContentUnwrapHandled;
import com.simplyti.service.clients.http.handler.HttpRequestFilterHandler;
import com.simplyti.service.clients.http.handler.SetHostHeaderHandler;
import com.simplyti.service.clients.http.sse.handler.HttpServerSentEventDecoder;
import com.simplyti.service.clients.http.sse.handler.HttpServerSentEventHandshakeHandled;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.service.filter.priority.Priorized;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.util.CharsetUtil;

public class HttpClientChannelPoolHandler extends AbstractClientPoolHandler {
	
	private static final ByteBuf DELIMITER = Unpooled.copiedBuffer("\n\n", CharsetUtil.UTF_8);
	private static final int DEFAULT_MAX_CONTENT_LENGTH = 52428800;

	private final SetHostHeaderHandler setHostHeaderHandler;
	private final List<HttpRequestFilter> filters;
	private final int maxContextLength;


	public HttpClientChannelPoolHandler(long readTimeoutMillis, boolean verbose, List<HttpRequestFilter> filters, int maxContextLength) {
		super(readTimeoutMillis, verbose);
		this.setHostHeaderHandler = new SetHostHeaderHandler();
		this.filters=filters==null?null:filters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
		this.maxContextLength=maxContextLength;
	}

	@Override
	public void channelCreated0(Channel ch) {
		ch.pipeline().addLast(new HttpClientCodec(),
				new HttpRequestFilterHandler(filters),
				setHostHeaderHandler,
				new HttpServerSentEventHandshakeHandled(),
				new DelimiterBasedFrameDecoder(100000, DELIMITER),
				new HttpServerSentEventDecoder(),
				new HttpClientFullResponseAggregator(maxContextLength>0? maxContextLength:DEFAULT_MAX_CONTENT_LENGTH),
				new HttpContentUnwrapHandled());
	}
	
}
