package com.simplyti.service.examples.filter;

import java.util.concurrent.TimeUnit;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.filter.FilterContext;
import com.simplyti.service.filter.http.HttpResponseFilter;

import io.netty.handler.codec.http.HttpResponse;

public class AsyncHttpResponseFilterModule extends AbstractModule implements HttpResponseFilter {

	@Override
	protected void configure() {
		Multibinder<HttpResponseFilter> fulters = Multibinder.newSetBinder(binder(), HttpResponseFilter.class);
		fulters.addBinding().to(AsyncHttpResponseFilterModule.class).in(Singleton.class);
	}

	@Override
	public void execute(FilterContext<HttpResponse> context) {
		context.channel().eventLoop().schedule(()->{
			context.object().headers().set("x-filter","hello");
			context.done();
		}, 1, TimeUnit.SECONDS);
		
	}


}