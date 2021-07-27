package com.simplyti.service.builder.di;

import javax.inject.Inject;
import javax.inject.Provider;

import io.netty.channel.EventLoop;

public class StartStopLoopProvider implements Provider<EventLoop>{
	
	private final NativeIO nativeIO;
	
	@Inject
	public StartStopLoopProvider(NativeIO nativeIO) {
		this.nativeIO = nativeIO;
	}
	
	@Override
	public EventLoop get() {
		return nativeIO.eventLoopGroup(1).next();
	}
	
}