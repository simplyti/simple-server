package com.simplyti.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.hook.ServerStartHook;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public class TestServerStartHookFailModule extends AbstractModule implements ServerStartHook {


	@Override
	protected void configure() {
		Multibinder<ServerStartHook> hooks = Multibinder.newSetBinder(binder(), ServerStartHook.class);
		hooks.addBinding().to(TestServerStartHookFailModule.class).in(Singleton.class);
		
	}

	@Override
	public Future<Void> executeStart(EventLoop startStopLoop) {
		return startStopLoop.newFailedFuture(new RuntimeException("Hook Error"));
	}

}
