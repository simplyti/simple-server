package com.simplyti.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.HttpResponseFilter;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

public class HttpResponseFilterModule extends AbstractModule implements HttpResponseFilter {

	@Override
	protected void configure() {
		Multibinder<HttpResponseFilter> fulters = Multibinder.newSetBinder(binder(), HttpResponseFilter.class);
		fulters.addBinding().to(HttpResponseFilterModule.class).in(Singleton.class);
	}

	@Override
	public void execute(FilterContext<FullHttpResponse> context) {
		context.object().headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
		context.done();
		
	}


}