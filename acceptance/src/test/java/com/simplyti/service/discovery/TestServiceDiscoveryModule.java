package com.simplyti.service.discovery;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.gateway.ServiceDiscovery;

public class TestServiceDiscoveryModule extends AbstractModule{

	@Override
	public void configure() {
		TestServiceDiscovery discovery =TestServiceDiscovery.getInstance(); 
		requestInjection(discovery);
		bind(ServiceDiscovery.class).toInstance(discovery);
		Multibinder.newSetBinder(binder(), ApiProvider.class)
			.addBinding().toInstance(b->b.when().get("/gateway")
					.then(ctx->ctx.send("Hello from gtw!")));
		
		Multibinder.newSetBinder(binder(), ApiProvider.class)
		.addBinding().toInstance(b->b.when().post("/gateway/echo")
				.then(ctx->ctx.send(ctx.body().copy())));
	}
	
}
