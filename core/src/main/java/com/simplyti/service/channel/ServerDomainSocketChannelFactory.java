package com.simplyti.service.channel;

import javax.inject.Inject;

import com.simplyti.service.builder.di.NativeIO;

import io.netty.channel.ChannelFactory;
import io.netty.channel.unix.ServerDomainSocketChannel;

public class ServerDomainSocketChannelFactory implements ChannelFactory<ServerDomainSocketChannel> {

	private final NativeIO nativeIO;

	@Inject
	public ServerDomainSocketChannelFactory(NativeIO nativeIO) {
		this.nativeIO=nativeIO;
	}

	@Override
	public ServerDomainSocketChannel newChannel() {
		return nativeIO.serverDomainChannel();
	}

}
