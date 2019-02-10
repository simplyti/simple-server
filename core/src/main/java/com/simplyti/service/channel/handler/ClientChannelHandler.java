package com.simplyti.service.channel.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.simplyti.service.Service;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.api.filter.HttpRequetFilter;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.handler.inits.ApiRequestHandlerInit;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ClientChannelHandler extends ChannelDuplexHandler {
	
	public static final String NAME = "request-hander";

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());

	private final Service<?> service;
	
	private final List<String> currentHandlers = new ArrayList<>();

	private final ApiRequestHandlerInit apiRequestHandlerInit;
	private final FileServerHandlerInit fileServerHandlerInit;
	private final DefaultBackendHandlerInit defaultBackendRequestHandlerInit;
	
	private final Set<HttpRequetFilter> filters;

	private boolean upgrading;

	public ClientChannelHandler(Service<?> service,
			ApiRequestHandlerInit apiRequestHandlerInit,
			FileServerHandlerInit fileServerHandlerInit,
			DefaultBackendHandlerInit defaultBackendRequestHandlerInit,
			Set<HttpRequetFilter> filters) {
		this.service=service;
		this.apiRequestHandlerInit=apiRequestHandlerInit;
		this.fileServerHandlerInit=fileServerHandlerInit;
		this.defaultBackendRequestHandlerInit=defaultBackendRequestHandlerInit;
		this.filters=filters;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(true);
			HttpRequest request = (HttpRequest) msg;
			if(filters.isEmpty()) {
				serviceProceed(ctx,request);
			}else {
				Future<Boolean> futureHandled = FilterChain.of(filters, ctx, request).execute();
				futureHandled.addListener(result->{
					if(result.isSuccess()) {
						if(!futureHandled.getNow()) {
							serviceProceed(ctx,request);
						}
					}else {
						ctx.fireExceptionCaught(result.cause());
					}
				});
			}
		}else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private void serviceProceed(ChannelHandlerContext ctx, HttpRequest request) {
		List<String> added;
		if(request.decoderResult().isFailure()) {
			ReferenceCountUtil.release(request);
			ctx.fireExceptionCaught(new BadRequestException());
		}else if((added=fileServerHandlerInit.canHandle(ctx,request,NAME))!=null){
			handle(ctx,request,added);
		} else if ((added=apiRequestHandlerInit.canHandle(ctx,request,NAME)) !=null) {
			handle(ctx,request,added);
		}else if((added=defaultBackendRequestHandlerInit.canHandle(ctx,request,NAME))!=null) {
			handle(ctx,request,added);
		} else {
			ReferenceCountUtil.release(request);
			ctx.fireExceptionCaught(new NotFoundException());
		}
	}

	private void handle(ChannelHandlerContext ctx, HttpRequest request, List<String> added) {
		currentHandlers.addAll(added);
		ctx.fireChannelRead(request);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof HttpResponse && "Upgrade".equalsIgnoreCase(((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION))) {
			this.upgrading=true;
		}
		
		if(!isContinue(msg) && msg instanceof LastHttpContent) {
			if(!upgrading) {
				currentHandlers.forEach(handler->ctx.pipeline().remove(handler));
				currentHandlers.clear();
			}
			promise.addListener(f->{
				if(upgrading) {
					ctx.pipeline().remove(HttpServerCodec.class);
					ctx.pipeline().remove(this);
				}
				ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(false);
				if(service.stopping()){
					log.info("Server is stopping, close channel");
					ctx.channel().close();
				}
			});
		}
		ctx.write(msg, promise);
	}

	private boolean isContinue(Object msg) {
		return msg instanceof HttpResponse && ((HttpResponse) msg).status().equals(HttpResponseStatus.CONTINUE);
	}
	
}
