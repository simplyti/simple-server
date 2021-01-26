package com.simplyti.service.channel.handler;

import com.simplyti.service.ServerStopAdvisor;
import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.exception.BadRequestException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ClientChannelHandler extends ChannelDuplexHandler {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(ClientChannelHandler.class);
	
	private final ServerStopAdvisor startStopMonitor;
	
	private boolean upgrading;

	public ClientChannelHandler(ServerStopAdvisor startStopMonitor) {
		this.startStopMonitor=startStopMonitor;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof HttpRequest) {
			ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(true);
			HttpRequest request = (HttpRequest) msg;
			serviceProceed(ctx,request);
		}else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private void serviceProceed(ChannelHandlerContext ctx, HttpRequest request) {
		if(request.decoderResult().isFailure()) {
			ReferenceCountUtil.release(request);
			ctx.fireExceptionCaught(new BadRequestException());
		}else {
			ctx.fireChannelRead(request);
		}
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(msg instanceof HttpResponse && "Upgrade".equalsIgnoreCase(((HttpResponse) msg).headers().get(HttpHeaderNames.CONNECTION))) {
			this.upgrading=true;
		}
		
		if(msg instanceof LastHttpContent) {
			if(!isContinue(msg)) {
				handleLastContent(ctx,promise);
			}
		}
		ctx.write(msg, promise);
	}
	
	private void handleLastContent(ChannelHandlerContext ctx, ChannelPromise promise) {
		promise.addListener(f->{
			if(upgrading) {
				if(ctx.pipeline().get("decoder")!=null) {
					ctx.pipeline().remove("encoder");
					ctx.pipeline().remove("decoder");
				}
				ctx.pipeline().remove(this);
			}
			
			ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).set(false);
			if(startStopMonitor.isStoping()){
				log.info("Server is stopping, close channel");
				ctx.channel().close();
			}
		});
	}

	private boolean isContinue(Object msg) {
		return msg instanceof HttpResponse && ((HttpResponse) msg).status().equals(HttpResponseStatus.CONTINUE);
	}

}
