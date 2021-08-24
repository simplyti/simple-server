package com.simplyti.service.examples.filter;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.exception.BadRequestException;
import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.FullHttpRequestFilter;

import io.netty.handler.codec.http.FullHttpRequest;

public class FullHttpRequestFilterModule extends AbstractModule implements FullHttpRequestFilter {

	@Override
	protected void configure() {
		Multibinder<FullHttpRequestFilter> fulters = Multibinder.newSetBinder(binder(), FullHttpRequestFilter.class);
		fulters.addBinding().to(FullHttpRequestFilterModule.class).in(Singleton.class);
	}

	@Override
	public void execute(FilterContext<FullHttpRequest> context) {
		if(context.object().uri().equals("/hello/bad") ||
				context.object().uri().equals("/echo/bad")){
			context.fail(new BadRequestException());
		}else {
			context.done();
		}
	}
	
}
