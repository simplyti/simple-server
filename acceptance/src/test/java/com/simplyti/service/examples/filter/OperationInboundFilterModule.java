package com.simplyti.service.examples.filter;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.server.http.api.filter.OperationInboundFilter;
import com.simplyti.server.http.api.handler.ApiInvocation;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.exception.UnauthorizedException;

import io.netty.handler.codec.http.HttpHeaderNames;

public class OperationInboundFilterModule extends AbstractModule implements OperationInboundFilter {

	@Override
	protected void configure() {
		Multibinder<OperationInboundFilter> fulters = Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		fulters.addBinding().to(OperationInboundFilterModule.class).in(Singleton.class);
		fulters.addBinding().toInstance(FilterContext::done);
	}

	@Override
	public void execute(FilterContext<ApiInvocation> context) {
		String authorization = context.object().headers().get(HttpHeaderNames.AUTHORIZATION);
		if(authorization==null) {
			context.fail(new UnauthorizedException());
		}else {
			context.done();
		}
	}

}