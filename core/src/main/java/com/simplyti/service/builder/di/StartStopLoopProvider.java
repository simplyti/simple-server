package com.simplyti.service.builder.di;

import static io.vavr.control.Try.of;

import java.lang.reflect.Constructor;

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
		Constructor<? extends EventLoopGroup> constructor = of(()->eventLoopGroup.getClass().getConstructor(Integer.TYPE)).get();
		return of(()->constructor.newInstance(number)).get();
	}

}