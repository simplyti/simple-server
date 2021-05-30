package com.simplyti.service.builder.di.guice.nativeio;

import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.simplyti.service.builder.di.ServerTransportProvider;
import com.simplyti.service.channel.ServerDomainSocketChannelFactory;
import com.simplyti.service.channel.ServerSocketChannelFactory;
import com.simplyti.service.transport.ServerTransport;

import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.unix.ServerDomainSocketChannel;

public class TransportModule extends AbstractModule {
	
	
	@Override
	protected void configure() {
		bind(new TypeLiteral<ChannelFactory<ServerChannel>>() {}).to(ServerSocketChannelFactory.class);
		bind(new TypeLiteral<ChannelFactory<ServerDomainSocketChannel>>() {}).to(ServerDomainSocketChannelFactory.class);
	
		bind(new TypeLiteral<Set<ServerTransport>>() {}).toProvider(ServerTransportProvider.class);
	}

}
