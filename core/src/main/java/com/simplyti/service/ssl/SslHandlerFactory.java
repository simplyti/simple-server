package com.simplyti.service.ssl;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslHandler;

public interface SslHandlerFactory {

	SslHandler handler(Channel channel);

}
