package com.simplyti.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.ApiOperation;
import com.simplyti.service.api.filter.FilterContext;
import com.simplyti.service.api.filter.OperationInboundFilter;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class TracingModule extends AbstractModule implements OperationInboundFilter {
	
	InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	@Override
	protected void configure() {
		Multibinder<OperationInboundFilter> filters = Multibinder.newSetBinder(binder(), OperationInboundFilter.class);
		filters.addBinding().to(TracingModule.class).in(Singleton.class);
	}

	@Override
	public void execute(FilterContext<ApiInvocation<?>> context) {
		ApiOperation<?, ?> operation = context.object().operation();
		log.info("[{}] {} {}",operation.meta("serviceId"), operation.method(),operation.pathPattern().template());
		context.done();
	}

}
