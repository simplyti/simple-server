package com.simplyti.service.gateway.handler;

import java.net.InetSocketAddress;

import com.simplyti.service.clients.Endpoint;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.LastHttpContent;

public class BackendProxyHandler extends ChannelDuplexHandler {

	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
	
	private final Channel frontendChannel;
	private final ChannelPool backendChannelPool;
	private final boolean frontSsl;
	
	private boolean upgrading;
	
	public BackendProxyHandler(ChannelPool backendChannelPool, Channel frontendChannel, Endpoint endpoint, boolean frontSsl) {
		this.frontendChannel = frontendChannel;
		this.backendChannelPool = backendChannelPool;
		this.frontSsl=frontSsl;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(false);
		ctx.channel().read();
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		frontendChannel.close();
    }
	
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof HttpRequest) {
        	InetSocketAddress inetSocket = (InetSocketAddress) frontendChannel.remoteAddress();
        	((HttpRequest) msg).headers().set(X_FORWARDED_FOR,inetSocket.getHostString());
        	((HttpRequest) msg).headers().set(X_FORWARDED_PROTO,frontSsl?HttpScheme.HTTPS.name():HttpScheme.HTTP.name());
        }
        ctx.write(msg, promise);
    }

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		frontendChannel.writeAndFlush(msg).addListener(f -> {
			if (f.isSuccess()) {
				ctx.channel().read();
			} else {
				ctx.channel().close();
			}
		});
		
		if(msg instanceof HttpResponse && "Upgrade".equalsIgnoreCase(((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION))) {
			this.upgrading=true;
		}else if(upgrading && msg instanceof LastHttpContent) {
			ctx.pipeline().remove(HttpClientCodec.class);
		}else if(msg instanceof LastHttpContent) {
			ctx.pipeline().remove(this);
			backendChannelPool.release(ctx.channel());
		}
	}
	
}
