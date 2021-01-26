package com.simplyti.service.clients.http.handler;

import com.simplyti.service.clients.http.HttpClientStreamEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpContentUnwrapHandled extends SimpleChannelInboundHandler<HttpContent> {
	
	private boolean streamed;
	
	public HttpContentUnwrapHandled() {
		super(false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
		if(streamed) {
			if(msg.content().isReadable()) {
				ctx.fireChannelRead(msg.content());
			} else {
				msg.release();
			}
			
			if(msg instanceof LastHttpContent) {
				this.streamed=false;
				ctx.pipeline().fireUserEventTriggered(HttpClientStreamEvent.STOP);
			}
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof HttpClientStreamEvent && ((HttpClientStreamEvent) evt).type() == HttpClientStreamEvent.Type.START) {
			this.streamed=true;
		} else {
			ctx.fireUserEventTriggered(evt);
		}
    }

}
