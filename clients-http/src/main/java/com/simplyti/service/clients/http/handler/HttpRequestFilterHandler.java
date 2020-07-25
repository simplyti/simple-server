package com.simplyti.service.clients.http.handler;

import java.util.List;

import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpRequest;

public class HttpRequestFilterHandler extends ChannelOutboundHandlerAdapter {


	private final List<HttpRequestFilter> filters;

	public HttpRequestFilterHandler(List<HttpRequestFilter> filters) {
		this.filters=filters;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		FilterChain<HttpRequest> chain = FilterChain.of(filters,ctx,(HttpRequest)msg);
		chain.execute()
			.thenAccept(handled->{
				if(!handled) {
					ctx.write(msg, promise);
				} else {
					promise.setSuccess(null);
				}
			})
			.onError(promise::setFailure);
	}

}
