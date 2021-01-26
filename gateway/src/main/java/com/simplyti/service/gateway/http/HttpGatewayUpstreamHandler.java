package com.simplyti.service.gateway.http;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.gateway.BackendServiceMatcher;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;

public class HttpGatewayUpstreamHandler extends ChannelDuplexHandler {
	
	private static final String UPGRADE = HttpHeaderValues.UPGRADE.toString();

	private final BackendServiceMatcher serviceMatch;
	private final Channel frontChannel;
	private final ClientChannel clientChannel;
	
	private boolean fullRequestSuccess;
	private boolean fullResponseSuccess;
	private boolean initialRequestSuccess;
	
	private boolean upgrading;
	
	public HttpGatewayUpstreamHandler(BackendServiceMatcher serviceMatch, Channel frontChannel, ClientChannel clientChannel) {
		this.serviceMatch=serviceMatch;
		this.frontChannel=frontChannel;
		this.clientChannel=clientChannel;
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
		return serviceMatch.rewrite(request);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		frontChannel.writeAndFlush(msg).addListener(f->handleFrontWrite(ctx,msg,f));
		if(msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
    		this.upgrading = UPGRADE.equalsIgnoreCase(response.headers().get(HttpHeaderNames.CONNECTION));
		} else if(msg instanceof LastHttpContent) {
			if(upgrading) {
				ctx.pipeline().remove(HttpClientCodec.class);
			} else {
				this.fullResponseSuccess = true;
				checkRelease(ctx);
			}
        }
	}
	
	private void handleFrontWrite(ChannelHandlerContext ctx, Object msg, Future<?> future) {
		if(future.isSuccess()) {
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
