package com.simplyti.service.gateway.handler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.gateway.BackendServiceMatcher;
import com.simplyti.service.gateway.GatewayConfig;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;

public class BackendProxyHandler extends ChannelDuplexHandler {

	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
	private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
	
	private final GatewayConfig config;
	private final Channel frontendChannel;
	private final ChannelPool backendChannelPool;
	private final boolean frontSsl;
	private final boolean isContinueExpected;
	private final BackendServiceMatcher serviceMatch;
	private final Endpoint endpoint;
	
	private boolean upgrading;
	private boolean isContinuing;
	private boolean keepAlive;
	
	
	public BackendProxyHandler(GatewayConfig config, ChannelPool backendChannelPool, Channel frontendChannel, Endpoint endpoint, boolean isContinueExpected, boolean frontSsl, BackendServiceMatcher serviceMatch) {
		this.config=config;
		this.frontendChannel = frontendChannel;
		this.backendChannelPool = backendChannelPool;
		this.frontSsl=frontSsl;
		this.isContinueExpected=isContinueExpected;
		this.serviceMatch=serviceMatch;
		this.endpoint=endpoint;
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
        	HttpRequest request = (HttpRequest) msg;
        	InetSocketAddress inetSocket = (InetSocketAddress) frontendChannel.remoteAddress();
        	if(!request.headers().contains(X_FORWARDED_HOST) && request.headers().contains(HttpHeaderNames.HOST)) {
        		request.headers().set(X_FORWARDED_HOST,request.headers().get(HttpHeaderNames.HOST));
        	}
        	if(!request.headers().contains(X_FORWARDED_PROTO)) {
        		request.headers().set(X_FORWARDED_PROTO,frontSsl?HttpScheme.HTTPS.name():HttpScheme.HTTP.name());
        	}
        	request.headers().set(X_FORWARDED_FOR,inetSocket.getHostString());
        	if(!config.keepOriginalHost()) {
        		request.headers().set(HttpHeaderNames.HOST,endpoint.address().host());
        	}
        	msg = serviceMatch.rewrite((HttpRequest) msg);
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
		
		if(isContinueExpected && isContinue(msg)) {
			this.isContinuing=true;
		}
		
		if(msg instanceof HttpResponse) {
			this.keepAlive = HttpUtil.isKeepAlive((HttpResponse)msg);
		}
		
		if(msg instanceof HttpResponse && "Upgrade".equalsIgnoreCase(((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION))) {
			this.upgrading=true;
		} else if(upgrading && msg instanceof LastHttpContent) {
			ctx.pipeline().remove(HttpClientCodec.class);
		} else if(msg instanceof LastHttpContent) {
			if (isContinuing) {
				isContinuing=false;
			} else if(keepAlive) {
				ctx.pipeline().remove(this);
				release(ctx.channel());
			} else {
				ctx.channel().close();
			}
		}
	}

	private void release(Channel channel) {
		if(config.releaseChannelGraceTime()>0) {
			channel.eventLoop().schedule(()->backendChannelPool.release(channel), config.releaseChannelGraceTime(), TimeUnit.MILLISECONDS);
		} else {
			backendChannelPool.release(channel);
		}
	}

	private boolean isContinue(Object msg) {
		return msg instanceof HttpResponse && ((HttpResponse) msg).status().equals(HttpResponseStatus.CONTINUE);
	}
	
}
