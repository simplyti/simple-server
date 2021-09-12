package com.simplyti.service.clients.http.handler;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.TcpAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslHandler;

@Sharable
public class SetHostHeaderHandler extends ChannelOutboundHandlerAdapter {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof HttpRequest) {
			Address address = ctx.channel().attr(ClientChannel.ADDRESS).get();
			if(address instanceof TcpAddress) {
				TcpAddress tcpAddress = (TcpAddress) address;
				HttpRequest request = (HttpRequest) msg;
				if (!request.headers().contains(HttpHeaderNames.HOST)) {
					request.headers().set(HttpHeaderNames.HOST, value(ctx.pipeline().get(SslHandler.class)!=null,tcpAddress));
				}
			}
		}
		ctx.write(msg, promise);
	}

	private String value(boolean isSsl, TcpAddress tcpAddress) {
		if((isSsl && tcpAddress.port() == 443) ||
				(!isSsl && tcpAddress.port() == 80)) {
			return tcpAddress.host();
		} else {
			return tcpAddress.host()+":"+tcpAddress.port();
		}
	}

}
