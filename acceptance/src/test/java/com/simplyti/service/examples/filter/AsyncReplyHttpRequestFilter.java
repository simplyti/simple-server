package com.simplyti.service.examples.filter;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class AsyncReplyHttpRequestFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		context.channel().eventLoop().schedule(()->{
			FullHttpResponse fullResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("Filter response!", CharsetUtil.UTF_8));
			fullResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,fullResponse.content().readableBytes());
			context.channel().writeAndFlush(fullResponse);
			context.done(true);
		}, 1, TimeUnit.SECONDS);
	}
}
