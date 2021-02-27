package com.simplyti.service.examples.filter;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HTTPHeadereFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		String value = context.object().headers().get("x-req-filter");
		if(value == null) {
			context.done();
		} else if(value.equals("resolve")) {
			context.channel().pipeline().fireChannelRead(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("Hello Resolved!", CharsetUtil.UTF_8)));
			context.done(true);
		} else if(value.equals("resolvedelay")) {
			context.channel().eventLoop().schedule(()->{
				context.channel().pipeline().fireChannelRead(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("Hello Resolved Delayed!", CharsetUtil.UTF_8)));
				context.done(true);
			}, 500, TimeUnit.MILLISECONDS);
		} else if(value.equals("delay")) {
			context.channel().eventLoop().schedule(()->{
				context.done();
			}, 500, TimeUnit.MILLISECONDS);
		} else {
			context.done();
		}
		
	}

}
