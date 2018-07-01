package com.simplyti.service.channel.handler;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class DefaultBackendFullRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

}
