package com.simplyti.service.gateway;

import javax.inject.Inject;

import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.clients.pending.PendingMessages;
import com.simplyti.service.exception.NotFoundException;
import com.simplyti.service.exception.ServiceException;
import com.simplyti.service.gateway.balancer.ServiceBalancer;
import com.simplyti.service.gateway.handler.BackendProxyHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class GatewayRequestHandler extends DefaultBackendRequestHandler {
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ServiceDiscovery serviceDiscovery;
	private final InternalClient client;

	private final PendingMessages pendingMessages = new PendingMessages();
	
	private Channel backendChannel;

	private boolean failed;

	@Inject
	public GatewayRequestHandler(InternalClient client, ServiceDiscovery serviceDiscovery) {
		this.client = client;
		this.serviceDiscovery = serviceDiscovery;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
			ServiceBalancer service = serviceDiscovery.get(request.headers().get(HttpHeaderNames.HOST),request.method(), decoder.path());
			if (service == null) {
				ctx.fireExceptionCaught(new NotFoundException());
			} else {
				Endpoint endpoint = service.next();
				if (endpoint == null) {
					ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.SERVICE_UNAVAILABLE));
				} else {
					connectToEndpoint(ctx, endpoint);
				}
			}
		}
		
		if (backendChannel != null) {
			backendChannel.writeAndFlush(ReferenceCountUtil.retain(msg)).addListener(f->handleWriteFuture(ctx,f));
		} else if(!failed){
			pendingMessages.pending(ctx.executor().newPromise(), msg);
		}
	}
	
	private void connectToEndpoint(ChannelHandlerContext ctx, Endpoint endpoint) {
		ChannelPool pool = client.pool(endpoint);
		Future<Channel> channelFuture = pool.acquire();
		if (channelFuture.isDone()) {
			handleBackendChannelFuture(ctx, channelFuture, pool, endpoint);
		} else {
			channelFuture.addListener(f -> handleBackendChannelFuture(ctx, channelFuture, pool, endpoint));
		}
	}
	
	private void handleBackendChannelFuture(ChannelHandlerContext ctx, Future<Channel> backendChannelFuture,
			ChannelPool pool, Endpoint endpoint) {
		if (backendChannelFuture.isSuccess()) {
			backendChannelFuture.getNow().pipeline().addLast(new BackendProxyHandler(pool, ctx.channel()));
			if(ctx.executor().inEventLoop()) {
				handleBackendChannelSuccess(ctx,backendChannelFuture.getNow());
			}else {
				ctx.executor().submit(()->handleBackendChannelSuccess(ctx,backendChannelFuture.getNow()));
			}
		}else {
			log.warn("Cannot connect to backend {}: {}", endpoint, backendChannelFuture.cause().toString());
			ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.BAD_GATEWAY));
			pendingMessages.fail(backendChannelFuture.cause());
			failed=true;
		}
	}
	
	private void handleBackendChannelSuccess(ChannelHandlerContext ctx,Channel backendChannel) {
		this.backendChannel=backendChannel;
		pendingMessages.write(backendChannel).addListener(f->handleWriteFuture(ctx, f));
	}

	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(false);
    }
	
	@Override
	 public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(true);
    }

	private void handleWriteFuture(ChannelHandlerContext ctx,Future<?> f) {
		if(f.isSuccess()) {
			ctx.channel().read();
		}else {
			ctx.channel().close();
		}
	}

}
