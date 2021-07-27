package com.simplyti.service.channel;

import javax.inject.Inject;

import com.simplyti.service.builder.di.NativeIO;

import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;

public class ServerSocketChannelFactory implements ChannelFactory<ServerChannel> {

	private final NativeIO nativeIO;

	@Inject
	public ServerSocketChannelFactory(NativeIO nativeIO) {
		this.nativeIO=nativeIO;
	}

	@Override
	public ServerChannel newChannel() {
		return nativeIO.serverChannel();
	}

}
