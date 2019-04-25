package com.simplyti.service.builder.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Provider;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor=@__(@Inject))
public class StartStopLoopProvider implements Provider<EventLoop>{
	
	private final EventLoopGroup eventLoopGroup;
	
	@Override
	public EventLoop get() {
		return eventLoopGroup(1).next();
	}
	
	private EventLoopGroup eventLoopGroup(int number) {
		try {
			Constructor<? extends EventLoopGroup> constructor = eventLoopGroup.getClass().getConstructor(Integer.TYPE);
			return constructor.newInstance(number);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

}