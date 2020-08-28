package com.simplyti.service.gateway.handler;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.gateway.BackendServiceMatcher;
import com.simplyti.service.gateway.GatewayConfig;
import com.simplyti.service.gateway.filter.BackendHttpRequestListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class BackendProxyHandler extends ChannelDuplexHandler {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(BackendProxyHandler.class);

	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
	private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
	private static final String UPGRADE = HttpHeaderValues.UPGRADE.toString();
	
	private final GatewayConfig config;
	private final Channel frontendChannel;
	private final ChannelPool backendChannelPool;
	private final boolean frontSsl;
	private final boolean isContinueExpected;
	private final BackendServiceMatcher serviceMatch;
	private final Endpoint endpoint;
	private final Set<BackendHttpRequestListener> backendRequestListeners;
	
	private boolean upgrading;
	private boolean isContinuing;
	private boolean keepAlive;

	private ChannelFuture pendingFrontWrite;
	
	public BackendProxyHandler(GatewayConfig config, ChannelPool backendChannelPool, Channel frontendChannel, Endpoint endpoint, boolean isContinueExpected, boolean frontSsl, BackendServiceMatcher serviceMatch,
			Set<BackendHttpRequestListener> backendRequestListeners) {
		this.config=config;
		this.frontendChannel = frontendChannel;
		this.backendChannelPool = backendChannelPool;
		this.frontSsl=frontSsl;
		this.isContinueExpected=isContinueExpected;
		this.serviceMatch=serviceMatch;
		this.endpoint=endpoint;
		this.backendRequestListeners=backendRequestListeners;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(false);
		ctx.channel().read();
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(this.pendingFrontWrite ==null || this.pendingFrontWrite.isDone()) {
			frontendChannel.close();
		} else {
			this.pendingFrontWrite.addListener(f->frontendChannel.close());
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
        	if(!this.backendRequestListeners.isEmpty()) {
				this.backendRequestListeners.forEach(l->{
					try {
						l.startRequest(ctx, request);
					} catch (Exception e) {
						log.warn("Error handling request listener: {}", e.getMessage());
					}
				});
			}
        }
        
        if(msg instanceof LastHttpContent && !this.backendRequestListeners.isEmpty()) {
        	promise.addListener(f->{
        		if(f.isSuccess()) {
        			this.backendRequestListeners.forEach(l->{
        				try {
        					l.endRequest(ctx);
        				} catch (Exception e) {
							log.warn("Error handling request listener: {}", e.getMessage());
						}
        			});
        		}
        	});
        }
        
        ctx.write(msg, promise);
    }

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
			this.keepAlive = HttpUtil.isKeepAlive(response);
			this.upgrading = UPGRADE.equalsIgnoreCase(((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION));
			if(isContinueExpected && response.status().equals(HttpResponseStatus.CONTINUE)) {
				this.isContinuing=true;
			}
			if(!this.backendRequestListeners.isEmpty()) {
				this.backendRequestListeners.forEach(l->{
					try {
						l.startResponse(ctx, response);
					} catch (Exception e) {
						log.warn("Error handling request listener: {}", e.getMessage());
					}
				});
			}
		}
		
		this.pendingFrontWrite = frontendChannel.writeAndFlush(msg).addListener(f -> {
			if (f.isSuccess()) {
				ctx.channel().read();
			} else {
				ctx.channel().close();
			}
		});
		
		if(upgrading && msg instanceof LastHttpContent) {
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
			if(!this.backendRequestListeners.isEmpty()) {
				this.backendRequestListeners.forEach(l->{
					try {
						l.endResponse(ctx);
					} catch (Exception e) {
						log.warn("Error handling request listener: {}", e.getMessage());
					}
				});
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

}
