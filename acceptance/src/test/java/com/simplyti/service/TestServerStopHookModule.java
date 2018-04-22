package com.simplyti.service;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.simplyti.service.hook.ServerStopHook;

import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;

public class TestServerStopHookModule extends AbstractModule implements ServerStopHook {

	private boolean invoked;

	@Override
	protected void configure() {
		Multibinder<ServerStopHook> hooks = Multibinder.newSetBinder(binder(), ServerStopHook.class);
		hooks.addBinding().toInstance(this);
	}

	@Override
	public Future<Void> executeStop(EventLoop startStopLoop) {
		invoked=true;
		return startStopLoop.newSucceededFuture(null);
	}
	
	public boolean wasInvoked() {
		return invoked;
	}

}
