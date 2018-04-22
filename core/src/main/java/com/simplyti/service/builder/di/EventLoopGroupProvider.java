package com.simplyti.service.builder.di;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.control.Try.of;

import javax.inject.Provider;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopGroupProvider implements Provider<EventLoopGroup>{
	
	private static Class<? extends EventLoopGroup> EVENT_LOOP_GROUP_CLASS = Match(Epoll.isAvailable()).of(
			Case($(Boolean.TRUE), EpollEventLoopGroup.class),
			Case($(), NioEventLoopGroup.class )
	);

	@Override
	public EventLoopGroup get() {
		return of(EVENT_LOOP_GROUP_CLASS::newInstance).get();
	}

}
