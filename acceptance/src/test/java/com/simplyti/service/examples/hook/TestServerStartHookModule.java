package com.simplyti.service.examples.hook;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.server.http.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.hook.ServerStartHook;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public class TestServerStartHookModule extends AbstractModule implements ServerStartHook, ApiProvider{

	private String value;

	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), ServerStartHook.class).addBinding().toInstance(this);
		Multibinder.newSetBinder(binder(), ApiProvider.class).addBinding().toInstance(this);
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
