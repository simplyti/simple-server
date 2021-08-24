package com.simplyti.service.examples.filter;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.handler.codec.http.HttpRequest;

public class AddDelayHttpRequestFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		context.channel().eventLoop().schedule(()->context.done(), 1, TimeUnit.SECONDS);
	}
}
