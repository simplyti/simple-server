package com.simplyti.service.clients.http.handler;

import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;

public class HttpServerSentEventHandshakeHandled extends SimpleChannelInboundHandler<HttpObject> {
	
	public enum Event {
		START
	}

	private boolean sse;
	private boolean error;
	
	public HttpServerSentEventHandshakeHandled() {
		super(false);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if(sse) {
			if(msg instanceof HttpResponse) {
				HttpResponse response = (HttpResponse) msg;
				if(isError(response.status().codeClass())) {
					this.error = true;
					throw new HttpException(response.status().code());
				}
			} else if(error) {
				ReferenceCountUtil.release(msg);
				if(msg instanceof LastHttpContent) {
					this.sse= false ;
					this.error = false;
				}
			} else if(msg instanceof HttpContent){
				ctx.fireChannelRead(((HttpContent) msg).content());
			} else {
				ReferenceCountUtil.release(msg);
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt == Event.START) {
			this.sse=true;
		} else {
			ctx.fireUserEventTriggered(evt);
		}
    }

}
