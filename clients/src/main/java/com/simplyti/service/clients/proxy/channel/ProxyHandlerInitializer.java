package com.simplyti.service.clients.proxy.channel;

import java.net.InetSocketAddress;

import com.simplyti.service.clients.proxy.Proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;

public class ProxyHandlerInitializer extends ChannelInitializer<Channel>{

	private final ChannelPoolHandler handler;
	private final NoResolvingSocketAddressUnwarpHandler noresolvingSocketAddressUnwrap;
	private final Proxy proxy;

	public ProxyHandlerInitializer(ChannelPoolHandler handler, Proxy proxy) {
		this.handler=handler;
		this.proxy=proxy;
		this.noresolvingSocketAddressUnwrap=new NoResolvingSocketAddressUnwarpHandler();
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addFirst(noresolvingSocketAddressUnwrap);
		ch.pipeline().addFirst(proxyHandler());
		handler.channelCreated(ch);
	}

	private ProxyHandler proxyHandler() {
		switch(proxy.type()) {
		case SOCKS5:
			if(proxy.username()!=null && proxy.password()!=null) {
				return new Socks5ProxyHandler(new InetSocketAddress(proxy.address().host(), proxy.address().port()),
						proxy.username(),proxy.password());
			}else {
				return new Socks5ProxyHandler(new InetSocketAddress(proxy.address().host(), proxy.address().port()));
			}
		case HTTP:
		default:
			if(proxy.username()!=null && proxy.password()!=null) {
				return new HttpProxyHandler(new InetSocketAddress(proxy.address().host(), proxy.address().port()),
						proxy.username(),proxy.password());
			}else {
				return new HttpProxyHandler(new InetSocketAddress(proxy.address().host(), proxy.address().port()));
			}
		}
	}

}
