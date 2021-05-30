package com.simplyti.service.builder.di;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import io.netty.channel.EventLoop;

public class StartStopLoopProvider implements Provider<EventLoop>{
	
	private final EventLoopGroupProvider eventLoopGroupProvider;
	
	@Inject
	public StartStopLoopProvider(Provider<Optional<NativeIO>> nativeIO) {
		this.eventLoopGroupProvider = new EventLoopGroupProvider(1,nativeIO);
	}
	
	@Override
	public EventLoop get() {
		return eventLoopGroupProvider.get().next();
	}
	
}