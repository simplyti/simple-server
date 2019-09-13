package com.simplyti.service.gateway;

import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.service.gateway.handler.BackendProxyHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class GatewayRequestHandler extends DefaultBackendRequestHandler {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(GatewayRequestHandler.class);
	
	private final ServiceDiscovery serviceDiscovery;
	private final InternalClient client;
	private final ServerConfig config;

	private final PendingMessages pendingMessages;
	
	private boolean frontSsl;
	private Channel backendChannel;
	private boolean ignoreNextMessages;


	@Inject
	public GatewayRequestHandler(InternalClient client, ServiceDiscovery serviceDiscovery, ServerConfig config) {
		super(false);
		this.client = client;
		this.config=config;
		this.serviceDiscovery = serviceDiscovery;
		this.pendingMessages = new PendingMessages();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			Future<BackendServiceMatcher> backendFuture = serviceDiscovery.get(request.headers().get(HttpHeaderNames.HOST),request.method(), request.uri(),ctx.executor());
			if(backendFuture.isDone()) {
				handleBackendMatch(backendFuture,ctx,request);
			}else {
				backendFuture.addListener(f->{
					handleBackendMatch(backendFuture,ctx,request);
				});
			}
		}
		
		if (backendChannel != null) {
			backendChannel.writeAndFlush(msg).addListener(f->handleWriteFuture(ctx,f));
		} else if(!ignoreNextMessages){
			pendingMessages.pending(ctx.executor().newPromise(), msg);
		}
	}
	
	private void handleBackendMatch(Future<BackendServiceMatcher> backendFuture, ChannelHandlerContext ctx, HttpRequest request) {
		if(!backendFuture.isSuccess()) {
			ctx.fireExceptionCaught(backendFuture.cause());
			pendingMessages.fail(backendFuture.cause());
			this.ignoreNextMessages=true;
			return;
		}
		
		BackendServiceMatcher service = backendFuture.getNow();
		
		if (service == null) {
			Throwable cause = new NotFoundException();
			ctx.fireExceptionCaught(cause);
			pendingMessages.fail(cause);
			this.ignoreNextMessages=true;
			return;
		}else {
			if(service.get().tlsEnabled() && !frontSsl) {
				if(handleSslRedirect(ctx,request,service.get())) {
					pendingMessages.fail(new RuntimeException("Redirected"));
					this.ignoreNextMessages=true;
					return;
				}
			}
			if(service.get().filters().isEmpty()) {
				serviceProceed(ctx,service,HttpUtil.is100ContinueExpected(request));
			}else {
				filterRequest(ctx,service,request);
			}
		}
	}

	private boolean handleSslRedirect(ChannelHandlerContext ctx, HttpRequest request,BackendService service) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PERMANENT_REDIRECT,
				Unpooled.EMPTY_BUFFER);
		String host = request.headers().get(HttpHeaderNames.HOST);
		if(host!=null) {
			String[] hostPort = host.split(":");
			response.headers().set(HttpHeaderNames.LOCATION,"https://"+hostPort[0]);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
			ctx.writeAndFlush(response).addListener(f->handleWriteFuture(ctx,f));
			return true;
		}else {
			return false;
		}
	}
	
	private void filterRequest(ChannelHandlerContext ctx, BackendServiceMatcher service, HttpRequest request) {
		Future<Boolean> futureHandled = FilterChain.of(service.get().filters(),ctx,service.rewrite(request)).execute();
		futureHandled.addListener(result->{
			if(ctx.executor().inEventLoop()) {
				handleFilterResult(ctx,futureHandled,service,HttpUtil.is100ContinueExpected(request));
			}else {
				ctx.executor().execute(()->handleFilterResult(ctx,futureHandled,service,HttpUtil.is100ContinueExpected(request)));
			}
		});
	}

	private void handleFilterResult(ChannelHandlerContext ctx, Future<Boolean> futureHandled, BackendServiceMatcher service, boolean isContinueExpected) {
		if(futureHandled.isSuccess()) {
			if(futureHandled.getNow()) {
				pendingMessages.fail(new RuntimeException("Handled by filter"));
				this.ignoreNextMessages=true;
			}else {
				serviceProceed(ctx,service,isContinueExpected);
			}
		}else {
			ctx.fireExceptionCaught(futureHandled.cause());
			pendingMessages.fail(futureHandled.cause());
			this.ignoreNextMessages=true;
		}
	}

	private void serviceProceed(ChannelHandlerContext ctx, BackendServiceMatcher service,boolean isContinueExpected) {
		Endpoint endpoint = service.get().loadBalander().next();
		if (endpoint == null) {
			ctx.fireExceptionCaught(new ServiceUnavailableException());
			pendingMessages.fail(new RuntimeException("No endpoints"));
			this.ignoreNextMessages=true;
		} else {
			connectToEndpoint(ctx, endpoint,isContinueExpected,service);
		}
	}

	private void connectToEndpoint(ChannelHandlerContext ctx, Endpoint endpoint, boolean isContinueExpected, BackendServiceMatcher serviceMatch) {
		ChannelPool pool = client.pool(endpoint);
		Future<Channel> channelFuture = pool.acquire();
		if (channelFuture.isDone()) {
			handleBackendChannelFuture(ctx, channelFuture, pool, endpoint, isContinueExpected, serviceMatch);
		} else {
			channelFuture.addListener(f -> handleBackendChannelFuture(ctx, channelFuture, pool, endpoint, isContinueExpected, serviceMatch));
		}
	}
	
	private void handleBackendChannelFuture(ChannelHandlerContext ctx, Future<Channel> backendChannelFuture,
			ChannelPool pool, Endpoint endpoint, boolean isContinueExpected, BackendServiceMatcher serviceMatch) {
		if(ctx.executor().inEventLoop()) {
			handleBackendChannelFuture0(ctx,backendChannelFuture,pool,endpoint, isContinueExpected, serviceMatch);
		}else {
			ctx.executor().execute(()->handleBackendChannelFuture0(ctx,backendChannelFuture,pool,endpoint,isContinueExpected, serviceMatch));
		}
	}
	
	private void handleBackendChannelFuture0(ChannelHandlerContext ctx, Future<Channel> backendChannelFuture,
			ChannelPool pool, Endpoint endpoint, boolean isContinueExpected, BackendServiceMatcher serviceMatch) {
		if (backendChannelFuture.isSuccess()) {
			backendChannelFuture.getNow().pipeline().addLast(new BackendProxyHandler(pool, ctx.channel(),endpoint,isContinueExpected,frontSsl,serviceMatch));
			this.backendChannel=backendChannelFuture.getNow();
			pendingMessages.write(backendChannel).addListener(f->handleWriteFuture(ctx, f));
		}else {
			log.warn("Cannot connect to backend {}: {}", endpoint, backendChannelFuture.cause().toString());
			ctx.fireExceptionCaught(new ServerErrorException(HttpResponseStatus.BAD_GATEWAY.code()));
			pendingMessages.fail(backendChannelFuture.cause());
			ignoreNextMessages=true;
		}
	}

	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().config().setAutoRead(false);
		InetSocketAddress localAddress = (InetSocketAddress) ctx.channel().localAddress();
		this.frontSsl = config.securedPort()==localAddress.getPort();
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
