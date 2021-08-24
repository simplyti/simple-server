package com.simplyti.service.gateway.http;

import java.util.function.Supplier;

import javax.inject.Inject;

import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.GenericClient;
import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.gateway.BackendServiceMatcher;
import com.simplyti.service.gateway.GatewayConfig;
import com.simplyti.service.gateway.ServiceDiscovery;
import com.simplyti.service.transport.ServerTransport;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

public class HttpGatewayRequestHandler extends ChannelDuplexHandler implements DefaultBackendRequestHandler {
	
	private final ServiceDiscovery serviceDiscovery;
	private final GenericClient httpGateway;
	private final GatewayConfig gatewayConfig;
	private final PendingMessages pendingMessages;
	
	private boolean frontSsl;
	private boolean failurePerpetially;
	private Channel gateway;
	
	@Inject
	public HttpGatewayRequestHandler(@HttpGatewayClient GenericClient httpGateway, ServiceDiscovery serviceDiscovery, GatewayConfig gatewayConfig) {
		this.httpGateway = httpGateway;
		this.serviceDiscovery=serviceDiscovery;
		this.pendingMessages = new PendingMessages();
		this.gatewayConfig=gatewayConfig;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		this.frontSsl = ctx.channel().parent().attr(ServerTransport.LISTENER).get().ssl();
    }
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			ctx.channel().config().setAutoRead(false);
			this.gateway=null;
			this.failurePerpetially=false;
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else {
			if (gateway != null) {
				gateway.writeAndFlush(msg).addListener(f->handlePartialWriteFuture(ctx,f, gateway));
			} else if(!failurePerpetially){
				pendingMessages.pending(ctx.executor().newPromise(), msg);
			} else {
				ReferenceCountUtil.release(msg);
			}
		}
	}
	
	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
		Future<BackendServiceMatcher> backendFuture = serviceDiscovery.get(host(ctx,request),request.method(), request.uri(),ctx.executor());
		if(backendFuture.isDone()) {
			handleBackendMatch(backendFuture,ctx,request);
		}else {
			backendFuture.addListener(f-> handleBackendMatch(backendFuture,ctx,request));
		}
	}
	
	private void handleBackendMatch(Future<BackendServiceMatcher> backendFuture, ChannelHandlerContext ctx, HttpRequest request) {
		if(!backendFuture.isSuccess()) {
			failurePrematurely(ctx, request, ()->response(HttpResponseStatus.SERVICE_UNAVAILABLE));
			return;
		}
		
		BackendServiceMatcher service = backendFuture.getNow();
		if(service == null) {
			failurePrematurely(ctx, request, ()->response(HttpResponseStatus.NOT_FOUND));
			return;
		}
		
		if(service.get().tlsEnabled() && !frontSsl) {
			failurePrematurely(ctx, request, ()->redirect(ctx,request));
			return;
		}
		
		if(!service.get().filters().isEmpty()){
			Future<Boolean> futureHandled = FilterChain.of(service.get().filters(), ctx, request).execute();
			futureHandled.addListener(f->handleRequestFilter(futureHandled,ctx, service, request));
			return;
		}
		
		serviceProceed(ctx,service,request, 1);
	}

	private void handleRequestFilter(Future<Boolean> future, ChannelHandlerContext ctx, BackendServiceMatcher service, HttpRequest request) {
		if(future.isSuccess()) {
			if(!future.getNow()) {
				serviceProceed(ctx,service,request, 1);
			} else{
				ignoreNextMessages(ctx);
			}
		} else {
			ignoreNextMessages(ctx);
			ctx.fireExceptionCaught(future.cause());
		}
	}

	private void serviceProceed(ChannelHandlerContext ctx, BackendServiceMatcher service, HttpRequest request, int retries) {
		Endpoint endpoint = service.get().loadBalander().next();
		if (endpoint == null) {
			failurePrematurely(ctx, request, ()->response(HttpResponseStatus.SERVICE_UNAVAILABLE));
		} else {
			serviceProceed(ctx, endpoint,service, request, retries);
		}
	}

	private void serviceProceed(ChannelHandlerContext ctx, Endpoint endpoint, BackendServiceMatcher service, HttpRequest request, int retries) {
		httpGateway.request()
			.withEndpoint(endpoint)
			.channel()
			.thenAccept(ch->{
				if(ch.eventLoop().inEventLoop()) {
					handleUpstreamConnection(ctx, ch, endpoint, service, request, retries);
				} else {
					ch.eventLoop().execute(()->handleUpstreamConnection(ctx, ch, endpoint, service, request, retries));
				}
			}).exceptionally(err->failurePrematurely(ctx, request, ()->response(HttpResponseStatus.BAD_GATEWAY)));
	}

	private void handleUpstreamConnection(ChannelHandlerContext ctx, ClientChannel ch, Endpoint endpoint, BackendServiceMatcher service, HttpRequest request, int retries) {
		if(ch.isActive()) {
			ch.pipeline().addLast(new HttpGatewayUpstreamHandler(service, endpoint, ctx.channel(), ch, gatewayConfig, frontSsl));
			ch.writeAndFlush(request).addListener(f->handleInitialWrite(ctx,service,request,ch,retries,f));
		} else {
			failurePrematurelyAndCloseBackend(ctx, request, HttpResponseStatus.SERVICE_UNAVAILABLE,ch);
		}
	}

	private void handleInitialWrite(ChannelHandlerContext ctx, BackendServiceMatcher service, HttpRequest request, ClientChannel ch, int retries, Future<?> writeFuture) {
		if(writeFuture.isSuccess()) {
			if(ctx.executor().inEventLoop()) {
				handleInitialWriteSuccess(ctx,ch);
			} else {
				ctx.executor().execute(()->handleInitialWriteSuccess(ctx,ch));
			}
		} else if(retries>0){
			serviceProceed(ctx, service, request, retries-1);
		} else {
			failurePrematurelyAndCloseBackend(ctx, request, HttpResponseStatus.SERVICE_UNAVAILABLE,ch);
		}
	}
	
	private void handleInitialWriteSuccess(ChannelHandlerContext ctx, ClientChannel ch) {
		this.gateway=ch.read();
		writePendingMessages(ctx, ch);
	}

	private void writePendingMessages(ChannelHandlerContext ctx, ClientChannel ch) {
		if(ctx.executor().inEventLoop()) {
			pendingMessages.write(ch).addListener(f->handlePartialWriteFuture(ctx, f, ch));
		} else {
			ctx.executor().execute(()->pendingMessages.write(ch).addListener(f->handlePartialWriteFuture(ctx, f, ch)));
		}
	}

	private void handlePartialWriteFuture(ChannelHandlerContext ctx, Future<?> writeFuture, Channel ch) {
		if(writeFuture.isSuccess()) {
			ctx.channel().read();
		} else {
			ctx.close();
			ch.close();
		}
	}
	
	private void failurePrematurelyAndCloseBackend(ChannelHandlerContext ctx, HttpRequest request, HttpResponseStatus status, ClientChannel ch) {
		failurePrematurely(ctx, request, ()->response(status));
		ch.close().addListener(f->ch.release());
	}
	
	private void ignoreNextMessages(ChannelHandlerContext ctx) {
		if(ctx.executor().inEventLoop()) {
			ignoreNextMessages0(ctx);
		} else {
			ctx.executor().execute(()->ignoreNextMessages0(ctx));
		}
	}

	private void ignoreNextMessages0(ChannelHandlerContext ctx) {
		this.failurePerpetially=true;
		pendingMessages.successDiscard();
		ctx.channel().config().setAutoRead(true);
	}

	private void failurePrematurely(ChannelHandlerContext ctx, HttpRequest request, Supplier<FullHttpResponse> responseSupplier) {
		ReferenceCountUtil.release(request);
		if(ctx.executor().inEventLoop()) {
			failurePrematurely0(ctx, responseSupplier);
		} else {
			ctx.executor().execute(()->failurePrematurely0(ctx, responseSupplier));
		}
	}
	
	private void failurePrematurely0(ChannelHandlerContext ctx, Supplier<FullHttpResponse> responseSupplier) {
		this.failurePerpetially=true;
		pendingMessages.fail(new RuntimeException("Prematurely failure"));
		ctx.writeAndFlush(responseSupplier.get()).addListener(f->{
			if(f.isSuccess()) {
				ctx.channel().config().setAutoRead(true);
			} else {
				ctx.close();
			}
		});
	}

	private FullHttpResponse response(HttpResponseStatus statusCode) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, statusCode, Unpooled.EMPTY_BUFFER, new DefaultHttpHeaders(false), EmptyHttpHeaders.INSTANCE);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
		return response;
	}
	
	private FullHttpResponse redirect(ChannelHandlerContext ctx, HttpRequest request) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.MOVED_PERMANENTLY, Unpooled.EMPTY_BUFFER, new DefaultHttpHeaders(false), EmptyHttpHeaders.INSTANCE);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
		response.headers().set(HttpHeaderNames.LOCATION,"https://"+host(ctx, request)+request.uri());
		return response;
	}

	private String host(ChannelHandlerContext ctx, HttpRequest request) {
		if(request.headers().contains(HttpGatewayUpstreamHandler.X_FORWARDED_HOST)) {
			return host(request.headers().get(HttpGatewayUpstreamHandler.X_FORWARDED_HOST));
		}
		if(request.headers().contains(HttpHeaderNames.HOST)) {
			return host(request.headers().get(HttpHeaderNames.HOST));
		}
		return ctx.channel().localAddress().toString();
	}

	private String host(String host) {
		if(host.contains(":")) {
			return host.split(":")[0];
		} else {
			return host;
		}
	}


}
