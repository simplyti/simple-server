package com.simplyti.service.clients.http.ws.handler;

import java.net.URI;
import java.util.function.Consumer;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.ClientChannelEvent;
import com.simplyti.service.clients.Endpoint;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class WebSocketChannelHandler extends SimpleChannelInboundHandler<Object> {
	
	private final Consumer<WebSocketFrame> consumer;
	private final WebSocketClientHandshaker handshaker;
	private final ClientRequestChannel<Void> clientChannel;

	public WebSocketChannelHandler(Endpoint endpoint, String uri, HttpHeaders headers, ClientRequestChannel<Void> channel, Consumer<WebSocketFrame> consumer) {
		this.consumer = consumer;
		this.clientChannel=channel;
		this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(
				URI.create("ws://"+endpoint.toString()+uri), WebSocketVersion.V13, null, false, headers, 1280000);
	}
	
	@Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientChannel.setSuccess(null);
    }
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!evt.equals(ClientChannelEvent.INIT)) {
			ctx.fireUserEventTriggered(evt);
		}
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
            ctx.fireUserEventTriggered(ClientChannelEvent.INIT);
            return;
        }
		
		if (msg instanceof FullHttpResponse) {
            throw new RuntimeException("Unexpected FullHttpResponse. Status=" + ((FullHttpResponse) msg).status());
        }else {
        		consumer.accept((WebSocketFrame) msg);
        }
	}

}
