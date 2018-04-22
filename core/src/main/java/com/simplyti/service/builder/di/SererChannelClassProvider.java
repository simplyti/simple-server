package com.simplyti.service.builder.di;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

import javax.inject.Inject;
import javax.inject.Provider;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor=@__(@Inject))
public class SererChannelClassProvider implements Provider<Class<? extends ServerSocketChannel>>{

	private final EventLoopGroup eventLoopGroup;
	
	@Override
	public Class<? extends ServerSocketChannel> get() {
		return Match(eventLoopGroup).of(
				Case($(instanceOf(EpollEventLoopGroup.class)), EpollServerSocketChannel.class),
				Case($(), NioServerSocketChannel.class));
	}

}
