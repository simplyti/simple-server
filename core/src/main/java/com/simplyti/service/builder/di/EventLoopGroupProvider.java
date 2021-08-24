package com.simplyti.service.builder.di;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import io.netty.channel.EventLoopGroup;

public class EventLoopGroupProvider implements Provider<EventLoopGroup>{
	
	private final int size;
	private final NativeIO nativeIO;
	private final Optional<EventLoopGroup> provided;

	@Inject
	public EventLoopGroupProvider(NativeIO nativeIO, @BuiltProvided Optional<EventLoopGroup> provided) {
		this(0,nativeIO, provided);
	}
	
	public EventLoopGroupProvider(int size, NativeIO nativeIO, Optional<EventLoopGroup> provided) {
		this.size=size;
		this.nativeIO=nativeIO;
		this.provided=provided;
	}

	@Override
	public EventLoopGroup get() {
		return provided.orElseGet(()->get0());
	}

	private EventLoopGroup get0() {
		return nativeIO.eventLoopGroup(size);
	}

}
