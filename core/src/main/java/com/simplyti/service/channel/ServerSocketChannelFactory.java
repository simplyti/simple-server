package com.simplyti.service.channel;

import java.nio.channels.spi.SelectorProvider;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.builder.di.NativeIO;

import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerSocketChannelFactory implements ChannelFactory<ServerChannel> {

	private final Provider<Optional<NativeIO>> nativeIO;

	@Inject
	public ServerSocketChannelFactory(Provider<Optional<NativeIO>> nativeIO) {
		this.nativeIO=nativeIO;
	}

	@Override
	public ServerChannel newChannel() {
		return nativeIO.get().map(n->n.serverChannel())
				.orElseGet(()->new NioServerSocketChannel(SelectorProvider.provider()));
	}

}
