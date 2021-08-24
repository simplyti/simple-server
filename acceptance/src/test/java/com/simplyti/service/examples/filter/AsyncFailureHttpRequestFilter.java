package com.simplyti.service.examples.filter;

import java.util.concurrent.TimeUnit;

import com.simplyti.service.exception.BadRequestException;
import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.handler.codec.http.HttpRequest;

public class AsyncFailureHttpRequestFilter implements HttpRequestFilter {

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		context.channel().eventLoop().schedule(()->context.fail(new BadRequestException()), 1, TimeUnit.SECONDS);
	}

}
