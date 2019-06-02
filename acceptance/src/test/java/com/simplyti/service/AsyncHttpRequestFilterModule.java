package com.simplyti.service;

import java.util.concurrent.TimeUnit;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.exception.BadRequestException;

import io.netty.handler.codec.http.HttpRequest;

public class AsyncHttpRequestFilterModule extends AbstractModule implements HttpRequestFilter {

	@Override
	protected void configure() {
		Multibinder<HttpRequestFilter> fulters = Multibinder.newSetBinder(binder(), HttpRequestFilter.class);
		fulters.addBinding().to(AsyncHttpRequestFilterModule.class).in(Singleton.class);
	}

	@Override
	public void execute(FilterContext<HttpRequest> context) {
		context.channel().eventLoop().schedule(()->doAsync(context), 1, TimeUnit.SECONDS);
	}

	private void doAsync(FilterContext<HttpRequest> context) {
		if(context.object().uri().equals("/hello/bad")){
			context.fail(new BadRequestException());
		}else {
			context.done();
		}
	}
	
}
