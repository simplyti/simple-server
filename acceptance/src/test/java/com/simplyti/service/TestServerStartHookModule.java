package com.simplyti.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.hook.ServerStartHook;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public class TestServerStartHookModule extends AbstractModule implements ServerStartHook, ApiProvider{

	private String value;

	@Override
	protected void configure() {
		Multibinder<ServerStartHook> hooks = Multibinder.newSetBinder(binder(), ServerStartHook.class);
		bind(TestServerStartHookModule.class).in(Singleton.class);
		hooks.addBinding().to(TestServerStartHookModule.class).in(Singleton.class);
		
	}

	@Override
	public Future<Void> executeStart(EventLoop startStopLoop) {
		this.value="HOOK!";
		return startStopLoop.newSucceededFuture(null);
	}

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/hook")
			.then(ctx->ctx.send(value));
		
	}

}
