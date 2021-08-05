package com.simplyti.service.proxy;

import javax.inject.Inject;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;

public class ProxyServer implements AutoCloseable {
	
	private final ChannelGroup group;

	@Inject
	@SneakyThrows
	public ProxyServer(EventLoopGroup eventLoopGroup) {
		EventLoop singleLoop = eventLoopGroup.next();
		ServerBootstrap proxy = new ServerBootstrap();
		proxy.group(singleLoop)
         	.channel(NioServerSocketChannel.class)
         	.childHandler(new SocksServerInitializer());
		this.group = new DefaultChannelGroup(singleLoop);
		
		group.add(proxy.bind(1080).sync().channel());
		group.add(proxy.bind(1081).sync().channel());
		group.add(proxy.bind(3128).sync().channel());
		group.add(proxy.bind(3129).sync().channel());
	}

	@Override
	public void close() throws Exception {
		group.close().sync();
	}

}
