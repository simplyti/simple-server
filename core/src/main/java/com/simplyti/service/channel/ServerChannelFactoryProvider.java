package com.simplyti.service.channel;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.builder.di.NativeIO;

import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor=@__(@Inject))
public class ServerChannelFactoryProvider implements Provider<ChannelFactory<ServerChannel>>{

	private final Optional<NativeIO> nativeIO;
	
	@Override
	public ChannelFactory<ServerChannel> get() {
		return new ServerSocketChannelFactory(nativeIO);
	}

}
