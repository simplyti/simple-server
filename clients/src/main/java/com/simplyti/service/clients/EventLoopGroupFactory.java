package com.simplyti.service.clients;

import io.netty.channel.EventLoopGroup;

public interface EventLoopGroupFactory {

	EventLoopGroup get();

}
