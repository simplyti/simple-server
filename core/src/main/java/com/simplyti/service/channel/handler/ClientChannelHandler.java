package com.simplyti.service.channel.handler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.filter.FilterChain;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.channel.pending.PendingMessages;
import com.simplyti.service.exception.BadRequestException;
import com.simplyti.service.exception.NotFoundException;
import com.simplyti.service.priority.Priorized;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ClientChannelHandler extends ChannelDuplexHandler {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(ClientChannelHandler.class);
	
	public static final String NAME = "request-hander";

	private final StartStopMonitor startStopMonitor;
	
	private final Set<HttpRequestFilter> requestFilters;
	private final Set<HttpResponseFilter> responseFilters;

	private final List<HandlerInit> handlers;
	
	private List<String> currentHandlers;

	private boolean expectedContinue;
	private boolean isContinuing;
	private boolean upgrading;

	private PendingMessages readPending;
	private PendingMessages writePending;
	private boolean flushed;

	public ClientChannelHandler(StartStopMonitor startStopMonitor, Set<HandlerInit> handlers,
			Set<HttpRequestFilter> requestFilters,Set<HttpResponseFilter> responseFilters) {
		this.startStopMonitor=startStopMonitor;
		this.handlers=handlers.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
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
		if(request.decoderResult().isFailure()) {
			ReferenceCountUtil.release(request);
			ctx.fireExceptionCaught(new BadRequestException());
		}else {
			handle(ctx,request);
		}
	}

	private void handle(ChannelHandlerContext ctx, HttpRequest request) {
		for(HandlerInit init:handlers) {
			List<String> addedHandlers = init.canHandle(ctx,request,NAME);
			if(addedHandlers!=null) {
				currentHandlers = addedHandlers;
				ctx.fireChannelRead(request);
				return;
			}
		}
		ReferenceCountUtil.release(request);
		ctx.fireExceptionCaught(new NotFoundException());
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
				promise.addListener(f->{
					if(upgrading) {
						ctx.pipeline().remove("encoder");
						ctx.pipeline().remove("decoder");
						ctx.pipeline().remove(this);
					} else {
						resetChannel(ctx.channel());
					}
					ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(false);
					if(startStopMonitor.isStoping()){
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

	public void resetChannel(Channel channel) {
		if(currentHandlers!=null) {
			for(String handler:currentHandlers) {
				channel.pipeline().remove(handler);
			}
			currentHandlers = null;
		}
	}
	
}
