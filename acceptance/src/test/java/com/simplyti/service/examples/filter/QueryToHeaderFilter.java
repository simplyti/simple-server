package com.simplyti.service.examples.filter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class QueryToHeaderFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		QueryStringDecoder decoder = new QueryStringDecoder(context.object().uri());
		if(decoder.parameters().containsKey("delay")) {
			int delay = Integer.parseInt(decoder.parameters().get("delay").get(0));
			context.object().headers().set("delayed", true);
			context.channel().eventLoop().schedule(()->handle(context, decoder.parameters(),true), delay, TimeUnit.MILLISECONDS);
		} else {
			handle(context, decoder.parameters(), false);
		}
	}

	private void handle(FilterContext<HttpRequest> context, Map<String, List<String>> parameters, boolean delayed) {
		if(parameters.containsKey("resolv")) {
			context.channel().pipeline().fireChannelRead(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK, Unpooled.copiedBuffer(delayed?"Filter delayed resolved!":"Filter resolved!", CharsetUtil.UTF_8)));
			context.done(true);
		} else {
			parameters.entrySet().stream()
				.filter(e->!e.getKey().equals("delay"))
				.forEach(e->context.object().headers().set(e.getKey(), e.getValue()));
			context.done();
		}
		
	}

}
