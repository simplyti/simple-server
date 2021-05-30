package com.simplyti.service.channel;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.builder.di.NativeIO;

import io.netty.channel.ChannelFactory;
import io.netty.channel.unix.ServerDomainSocketChannel;

public class ServerDomainSocketChannelFactory implements ChannelFactory<ServerDomainSocketChannel> {

	private final Provider<Optional<NativeIO>> nativeIO;

	@Inject
	public ServerDomainSocketChannelFactory(Provider<Optional<NativeIO>> nativeIO) {
		this.nativeIO=nativeIO;
	}

	@Override
	public ServerDomainSocketChannel newChannel() {
		return nativeIO.get().map(n->n.serverDomainChannel())
				.orElseThrow(()->new IllegalStateException());
	}

}
