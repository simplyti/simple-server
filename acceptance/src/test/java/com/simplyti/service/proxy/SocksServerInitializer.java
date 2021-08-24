package com.simplyti.service.proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class SocksServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
    	if(ch.localAddress().getPort() == 1080 || ch.localAddress().getPort() == 1081) {
    		ch.pipeline().addLast(new SocksPortUnificationServerHandler());
    		ch.pipeline().addLast(SocksServerHandler.INSTANCE);
    	} else {
    		ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
    		ch.pipeline().addLast(new HttpServerCodec());
            ch.pipeline().addLast(new HttpProxyServerHandler());
    	}
        
    }
}