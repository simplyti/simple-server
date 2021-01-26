package com.simplyti.service.builder.di.guice.nativeio;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.simplyti.service.builder.di.NativeIO;
import com.simplyti.service.builder.di.ServerTransportProvider;
import com.simplyti.service.channel.ServerSocketChannelFactory;
import com.simplyti.service.transport.ServerTransport;

import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;

public class TransportModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(new TypeLiteral<ChannelFactory<ServerChannel>>() {}).to(ServerSocketChannelFactory.class);
		bind(ServerTransport.class).toProvider(ServerTransportProvider.class);
		bind(NativeIO.class).in(Singleton.class);
	}

}
