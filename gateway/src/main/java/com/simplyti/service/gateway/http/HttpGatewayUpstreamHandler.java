package com.simplyti.service.gateway.http;

import java.net.InetSocketAddress;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.endpoint.TcpAddress;
import com.simplyti.service.gateway.BackendServiceMatcher;
import com.simplyti.service.gateway.GatewayConfig;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;

public class HttpGatewayUpstreamHandler extends ChannelDuplexHandler {
	
	private static final String X_FORWARDED_FOR = "x-forwarded-for";
	private static final String X_FORWARDED_PROTO = "x-forwarded-proto";
	public static final String X_FORWARDED_HOST = "x-forwarded-host";
	private static final String UPGRADE = HttpHeaderValues.UPGRADE.toString();

	private final GatewayConfig config;
	private final BackendServiceMatcher serviceMatch;
	private final Endpoint endpoint;
	private final Channel frontChannel;
	private final ClientChannel clientChannel;
	private final InetSocketAddress frontAddress;
	private final boolean frontSsl;
	
	private boolean fullRequestSuccess;
	private boolean fullResponseSuccess;
	private boolean initialRequestSuccess;
	
	private boolean upgrading;
	private boolean isContinue;

	
	public HttpGatewayUpstreamHandler(BackendServiceMatcher serviceMatch, Endpoint endpoint, Channel frontChannel, ClientChannel clientChannel, GatewayConfig config, boolean frontSsl) {
		this.serviceMatch=serviceMatch;
		this.endpoint=endpoint;
		this.frontChannel=frontChannel;
		this.clientChannel=clientChannel;
		this.frontSsl=frontSsl;
		this.config=config;
		this.frontAddress =  (InetSocketAddress) frontChannel.remoteAddress();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientChannel.release();
		if(initialRequestSuccess) {
			frontChannel.close();
		} 
	}
	
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		if(!ctx.channel().isActive()) {
			ctx.channel().pipeline().remove(this);
			channelInactive(ctx);
		}
	}
	
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    	if(msg instanceof HttpRequest) {
    		ctx.write(adaptProxiedRequest((HttpRequest) msg), promise).addListener(f->{
    			if(f.isSuccess()) {
    				this.initialRequestSuccess=true;
    			}
    		});
    	} else if(msg instanceof LastHttpContent) {
    		ctx.write(msg, promise).addListener(f->{
    			if(f.isSuccess()) {
    				this.fullRequestSuccess=true;
            		checkRelease(ctx);
    			}
    		});
    	} else {
    		ctx.write(msg, promise);
    	}
    }
    
	private HttpRequest adaptProxiedRequest(HttpRequest request) {
		setXForwardedFor(request);
    	setXForwardedProto(request);
    	setXForwardedHost(request);
    	setHost(request);
		return serviceMatch.rewrite(request);
	}

	private void setHost(HttpRequest request) {
    	if(!config.keepOriginalHost()) {
    		request.headers().set(HttpHeaderNames.HOST, ((TcpAddress)endpoint.address()).host());
    	}
	}

	private void setXForwardedHost(HttpRequest request) {
    	if(!request.headers().contains(X_FORWARDED_HOST) && request.headers().contains(HttpHeaderNames.HOST)) {
    		request.headers().set(X_FORWARDED_HOST,request.headers().get(HttpHeaderNames.HOST));
    	}
	}

	private void setXForwardedProto(HttpRequest request) {
		if(!request.headers().contains(X_FORWARDED_PROTO)) {
    		request.headers().set(X_FORWARDED_PROTO,frontSsl?HttpScheme.HTTPS.name():HttpScheme.HTTP.name());
    	}
	}

	private void setXForwardedFor(HttpRequest request) {
		if(request.headers().contains(X_FORWARDED_FOR)) {
			request.headers().set(X_FORWARDED_FOR,request.headers().get(X_FORWARDED_FOR)+", "+frontAddress.getHostString());
		} else {
			request.headers().set(X_FORWARDED_FOR,frontAddress.getHostString());
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if(msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
    		this.upgrading = UPGRADE.equalsIgnoreCase(response.headers().get(HttpHeaderNames.CONNECTION));
    		this.isContinue = response.status().equals(HttpResponseStatus.CONTINUE);
		} 
		
		if(msg instanceof LastHttpContent && !upgrading && !isContinue) {
			this.fullResponseSuccess = true;
			checkRelease(ctx);
        }
		
		
		frontChannel.writeAndFlush(msg).addListener(f->handleFrontWrite(ctx,msg,f));
		
		if(msg instanceof LastHttpContent && upgrading) {
			ctx.pipeline().remove(HttpClientCodec.class);
        }
	}
	
	private void handleFrontWrite(ChannelHandlerContext ctx, Object msg, Future<?> future) {
		if(future.isSuccess()) {
			if(upgrading && msg instanceof LastHttpContent) {
				frontChannel.pipeline().remove(HttpServerCodec.class);
			}
			ctx.read();
		} else {
			new RuntimeException("Cannot write to front channel",future.cause()).printStackTrace();
		}
	}

	private void checkRelease(ChannelHandlerContext ctx) {
		if(this.fullRequestSuccess && this.fullResponseSuccess) {
			ctx.pipeline().remove(this);
			this.clientChannel.release();
			this.frontChannel.config().setAutoRead(true);
		} 
	}

}
