package com.simplyti.service.clients.http.websocket.handler;

import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.concurrent.Promise;

public class WebSocketChannelHandler extends SimpleChannelInboundHandler<Object> {

	private final Promise<ClientChannel> handshakePromise;
	private final WebSocketClientHandshaker handshaker;
	private final ClientChannel channel;
	private final Promise<Void> promise;
	
	private final Consumer<ByteBuf> consumer;
	
	public WebSocketChannelHandler(String uri, ClientChannel channel, Promise<ClientChannel> handshakePromise, Consumer<ByteBuf> consumer, Promise<Void> promise) {
		this.handshakePromise=handshakePromise;
		this.channel=channel;
		this.consumer=consumer;
		this.promise=promise;
		this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(URI.create("ws://"+channel.address().toString()+uri), WebSocketVersion.V13, null, false, null, 1280000);
	}
	
	@Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(!handshakePromise.isDone()) {
			handshakePromise.setFailure(new ClosedChannelException());
			promise.setFailure(new ClosedChannelException());
		} else {
			promise.setSuccess(null);
		}
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!handshaker.isHandshakeComplete()) {
            try{
            	handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
            	handshakePromise.setSuccess(channel);
            }catch(WebSocketHandshakeException cause) {
            	exceptionCaught(ctx, cause);
            }
            return;
        }
		
		if (msg instanceof FullHttpResponse) {
			exceptionCaught(ctx, new RuntimeException("Unexpected FullHttpResponse. Status=" + ((FullHttpResponse) msg).status()));
        }else {
        	consumer.accept(((WebSocketFrame) msg).content());
        }
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.pipeline().remove(this);
		if(!handshakePromise.isDone()) {
			handshakePromise.setFailure(cause);
		}
		promise.setFailure(cause);
	}

}
