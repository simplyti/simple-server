package com.simplyti.service.channel.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.simplyti.service.Service;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.handler.inits.ApiRequestHandlerInit;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;
import com.simplyti.service.channel.pending.PendingMessages;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
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
	
	private final Set<HttpRequestFilter> requestFilters;
	private final Set<HttpResponseFilter> responseFilters;

	private boolean expectedContinue;
	private boolean isContinuing;
	private boolean upgrading;

	private PendingMessages readPending;
	
	private PendingMessages writePending;
	private boolean flushed;


	public ClientChannelHandler(Service<?> service,
			ApiRequestHandlerInit apiRequestHandlerInit,
			FileServerHandlerInit fileServerHandlerInit,
			DefaultBackendHandlerInit defaultBackendRequestHandlerInit,
			Set<HttpRequestFilter> requestFilters,Set<HttpResponseFilter> responseFilters) {
		this.service=service;
		this.apiRequestHandlerInit=apiRequestHandlerInit;
		this.fileServerHandlerInit=fileServerHandlerInit;
		this.defaultBackendRequestHandlerInit=defaultBackendRequestHandlerInit;
		this.requestFilters=requestFilters;
		this.responseFilters=responseFilters;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(true);
			HttpRequest request = (HttpRequest) msg;
			this.expectedContinue=HttpUtil.is100ContinueExpected(request);
			if(requestFilters.isEmpty()) {
				serviceProceed(ctx,request);
			}else {
				Future<Boolean> futureHandled = FilterChain.of(requestFilters, ctx, request).execute();
				this.readPending=new PendingMessages();
				futureHandled.addListener(f->handleRequestFilter(futureHandled,ctx,request));
			}
		}else if(readPending!=null) {
			this.readPending.pending(msg);
		}else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private void handleRequestFilter(Future<Boolean> future, ChannelHandlerContext ctx, HttpRequest request) {
		if(future.isSuccess()) {
			if(!future.getNow()) {
				serviceProceed(ctx,request);
				readPending.forEach(msg->ctx.fireChannelRead(msg.msg()));
			}else {
				readPending.release();
			}
		}else {
			readPending.release();
			ctx.fireExceptionCaught(future.cause());
		}
		readPending=null;
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
		if(responseFilters.isEmpty()) {
			responseProceed(ctx,msg,promise);
		}else if(msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
			Future<Boolean> futureHandled = FilterChain.of(responseFilters, ctx, response).execute();
			this.writePending=new PendingMessages();
			futureHandled.addListener(f->handleResponseFilter(futureHandled,ctx,response,promise));
		}else if(writePending!=null) {
			this.writePending.pending(msg,promise);
		}else {
			responseProceed(ctx,msg,promise);
		}
	}
	
	@Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
		if(writePending==null) {
			ctx.flush();
		} else {
			this.flushed=true;
		}
    }
	
	private void handleResponseFilter(Future<Boolean> future, ChannelHandlerContext ctx, HttpResponse response, ChannelPromise promise) {
		if(!future.isSuccess()) {
			log.warn("Error executing response filters: {}",future.cause().getMessage());
		}
		responseProceed(ctx,response,promise);
		writePending.forEach(msg->responseProceed(ctx,msg.msg(),msg.promise()));
		if(flushed) {
			ctx.flush();
		}
		writePending=null;
	}

	private void responseProceed(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		if(expectedContinue && isContinue(msg)) {
			this.isContinuing=true;
		} else if(msg instanceof HttpResponse && "Upgrade".equalsIgnoreCase(((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION))) {
			this.upgrading=true;
		}
		
		if(msg instanceof LastHttpContent) {
			if(isContinuing) {
				isContinuing=false;
			}else {
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
		}
		ctx.write(msg, promise);
	}

	private boolean isContinue(Object msg) {
		return msg instanceof HttpResponse && ((HttpResponse) msg).status().equals(HttpResponseStatus.CONTINUE);
	}
	
}
