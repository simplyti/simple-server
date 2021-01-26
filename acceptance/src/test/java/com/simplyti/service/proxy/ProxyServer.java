package com.simplyti.service.proxy;

import javax.inject.Inject;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;

public class ProxyServer {
	
	@Inject
	@SneakyThrows
	public ProxyServer(EventLoopGroup eventLoopGroup) {
		ServerBootstrap proxy = new ServerBootstrap();
		proxy.group(eventLoopGroup)
         .channel(NioServerSocketChannel.class)
         .childHandler(new SocksServerInitializer());
		proxy.bind(1080).sync();
		proxy.bind(1081).sync();
		proxy.bind(3128).sync();
		proxy.bind(3129).sync();
		
		
	}

}
