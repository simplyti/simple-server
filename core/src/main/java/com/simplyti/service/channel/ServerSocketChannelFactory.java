package com.simplyti.service.channel;

import java.nio.channels.spi.SelectorProvider;
import java.util.Optional;

import com.simplyti.service.builder.di.NativeIO;

import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerSocketChannelFactory implements ChannelFactory<ServerChannel> {

	private final Optional<NativeIO> nativeIO;

	public ServerSocketChannelFactory(Optional<NativeIO> nativeIO) {
		this.nativeIO=nativeIO;
	}

	@Override
	public ServerChannel newChannel() {
		return nativeIO.map(n->n.serverChannel())
				.orElseGet(()->new NioServerSocketChannel(SelectorProvider.provider()));
	}

}
