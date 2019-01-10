package com.simplyti.service.channel.handler;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.Service;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.channel.handler.inits.ApiRequestHandlerInit;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;
import com.simplyti.service.exception.BadRequestException;
import com.simplyti.service.exception.NotFoundException;

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
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ClientChannelHandler extends ChannelDuplexHandler {
	
	public static final String NAME = "request-hander";

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());

	private final Service<?> service;
	
	private final List<String> currentHandlers = new ArrayList<>();

	private final ApiRequestHandlerInit apiRequestHandlerInit;
	private final FileServerHandlerInit fileServerHandlerInit;
	private final DefaultBackendHandlerInit defaultBackendFullRequestHandlerInit;

	private boolean upgrading;

	public ClientChannelHandler(Service<?> service,
			ApiRequestHandlerInit apiRequestHandlerInit,
			FileServerHandlerInit fileServerHandlerInit,
			DefaultBackendHandlerInit defaultBackendFullRequestHandlerInit) {
		this.service=service;
		this.apiRequestHandlerInit=apiRequestHandlerInit;
		this.fileServerHandlerInit=fileServerHandlerInit;
		this.defaultBackendFullRequestHandlerInit=defaultBackendFullRequestHandlerInit;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(true);
			HttpRequest request = (HttpRequest) msg;
			List<String> added;
			if(request.decoderResult().isFailure()) {
				ReferenceCountUtil.release(msg);
				ctx.fireExceptionCaught(new BadRequestException());
			}else if((added=fileServerHandlerInit.canHandle(ctx,request,NAME))!=null){
				handle(ctx,msg,added);
			} else if ((added=apiRequestHandlerInit.canHandle(ctx,request,NAME)) !=null) {
				handle(ctx,msg,added);
			}else if((added=defaultBackendFullRequestHandlerInit.canHandle(ctx,request,NAME))!=null) {
				handle(ctx,msg,added);
			} else {
				ReferenceCountUtil.release(msg);
				ctx.fireExceptionCaught(new NotFoundException());
			}
		}else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private void handle(ChannelHandlerContext ctx, Object msg, List<String> added) {
		currentHandlers.addAll(added);
		ctx.fireChannelRead(msg);
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
