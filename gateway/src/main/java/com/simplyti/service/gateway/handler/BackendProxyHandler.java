package com.simplyti.service.gateway.handler;

import java.net.InetSocketAddress;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.gateway.BackendServiceMatcher;
import com.simplyti.service.gateway.GatewayConfig;
import com.simplyti.service.gateway.GatewayRequestHandlerOld;
import com.simplyti.service.gateway.http.HttpGatewayRequestHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
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
	private static final String UPGRADE = HttpHeaderValues.UPGRADE.toString();
	
	private final GatewayConfig config;
	private final Channel frontendChannel;
	private final ClientChannel clientChannel;
	private final boolean frontSsl;
	private final boolean isContinueExpected;
	private final BackendServiceMatcher serviceMatch;
	private final Endpoint endpoint;
	private final GatewayRequestHandlerOld gatewayHandler;
	
	private boolean upgrading;
	private boolean isContinuing;
	private boolean keepAlive;
	private ChannelFuture pendingFrontWrite;
	
	private boolean initialSuccess;
	
	public BackendProxyHandler(GatewayConfig config, ClientChannel clientChannel, Channel frontendChannel, Endpoint endpoint, boolean isContinueExpected, boolean frontSsl, BackendServiceMatcher serviceMatch, GatewayRequestHandlerOld gatewayHandler) {
		this.config=config;
		this.frontendChannel = frontendChannel;
		this.clientChannel = clientChannel;
		this.frontSsl=frontSsl;
		this.isContinueExpected=isContinueExpected;
		this.serviceMatch=serviceMatch;
		this.endpoint=endpoint;
		this.gatewayHandler=gatewayHandler;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		//ctx.channel().config().setAutoRead(false);
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(!this.initialSuccess) {
			return;
		}
		
		if(this.pendingFrontWrite ==null || this.pendingFrontWrite.isDone()) {
			this.frontendChannel.close();
		} else {
			this.pendingFrontWrite.addListener(f-> frontendChannel.close());
		}
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
        	
        	ctx.write(msg, promise).addListener(f->{
        		if(f.isSuccess()) {
        			this.initialSuccess = true;
        		}
        	});
        } else {
        	ctx.write(msg, promise);
        }
        
        
        
        
    }

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpResponse) {
			handleHttpResponse((HttpResponse)msg);
		}
		
		this.pendingFrontWrite = frontendChannel.writeAndFlush(msg).addListener(f -> {
			if (f.isSuccess()) {
				if(msg instanceof LastHttpContent) {
					System.out.println("#### ["+Thread.currentThread().getName()+"] LAST CONENT RESP "+ctx.channel().id());
					if(ctx.channel().isActive()) {
						ctx.pipeline().remove(this);
					}
					clientChannel.release();
				}
				//ctx.channel().read();
			} else {
				ctx.channel().close();
			}
		});
		
		if(msg instanceof LastHttpContent) {
			handleLastHttpContent(ctx);
		}
	}
	
	private void handleLastHttpContent(ChannelHandlerContext ctx) {
		if(upgrading) {
			ctx.pipeline().remove(HttpClientCodec.class);
		} else if (isContinuing) {
			isContinuing=false;
		} else if(keepAlive) {
			//ctx.pipeline().remove(this);
			//clientChannel.release();
			//gatewayHandler.release();
		} else {
			ctx.channel().close();
		}
	}

	private void handleHttpResponse(HttpResponse response) {
		this.keepAlive = HttpUtil.isKeepAlive(response);
		this.upgrading = UPGRADE.equalsIgnoreCase(response.headers().get(HttpHeaderNames.CONNECTION));
		if(isContinueExpected && response.status().equals(HttpResponseStatus.CONTINUE)) {
			this.isContinuing=true;
		}
	}
	
}
