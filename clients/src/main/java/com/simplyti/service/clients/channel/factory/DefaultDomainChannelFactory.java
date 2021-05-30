package com.simplyti.service.clients.channel.factory;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.unix.DomainSocketChannel;

public class DefaultDomainChannelFactory implements ChannelFactory<DomainSocketChannel> {

	private final NativeDomainChannelFactory nativeChannelFactory;

	public DefaultDomainChannelFactory(EventLoopGroup eventLoopGroup) {
		this.nativeChannelFactory = new NativeDomainChannelFactory(eventLoopGroup);
	}

	@Override
	public DomainSocketChannel newChannel() {
		DomainSocketChannel channel = this.nativeChannelFactory.newChannel();
		if(channel!=null) {
			return channel;
		} else {
			throw new IllegalStateException();
		}
	}
	
}