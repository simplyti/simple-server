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

public class AddHTTPHeadereFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		if(context.object().uri().endsWith("x-filter-name")){
			context.object().headers().set("x-filter-name","Pepe");
			context.done();
		} else if(context.object().uri().endsWith("x-filter-name-resolve")){
			context.channel().pipeline().fireChannelRead(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("Hello Resolved: Pepe", CharsetUtil.UTF_8)));
			context.done(true);
		} else if(context.object().uri().endsWith("x-filter-name-resolve-delay")){
			context.channel().eventLoop().schedule(()->{
				context.channel().pipeline().fireChannelRead(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer("Hello Resolved Delayed: Pepe", CharsetUtil.UTF_8)));
				context.done(true);
			}, 500, TimeUnit.MILLISECONDS);
		} else if(context.object().uri().endsWith("x-filter-name-delay")){
			context.channel().eventLoop().schedule(()->{
				context.object().headers().set("x-filter-name-delay","Delayed: Pepe");
				context.done();
			}, 500, TimeUnit.MILLISECONDS);
		} else {
			context.done();
		}
	}

}
