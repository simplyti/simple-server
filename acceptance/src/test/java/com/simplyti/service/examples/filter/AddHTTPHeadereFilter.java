package com.simplyti.service.examples.filter;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.handler.codec.http.HttpRequest;

public class AddHTTPHeadereFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		if(context.object().uri().endsWith("x-filter-name")){
			context.object().headers().set("x-filter-name","Pepe");
			context.done();
		} else if(context.object().uri().endsWith("x-filter-name-delay")){
			context.channel().eventLoop().schedule(()->{
				context.object().headers().set("x-filter-name-delay","Delayed: Pepe");
				context.done();
			}, 500, TimeUnit.MILLISECONDS);
		}
	}

}
