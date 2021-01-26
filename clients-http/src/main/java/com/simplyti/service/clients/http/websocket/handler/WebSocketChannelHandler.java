package com.simplyti.service.clients.http.websocket.handler;

import java.net.URI;
import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.AbstractBaseHttpResponseHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

public class WebSocketChannelHandler extends AbstractBaseHttpResponseHandler<Object> {

	private final WebSocketClientHandshaker handshaker;
	private final ClientChannel channel;
	private final Promise<ClientChannel> promise;
	private final Consumer<WebSocketFrame> frameConsumer;
	private final Promise<Void> closePromise;
	
	public WebSocketChannelHandler(String uri, ClientChannel channel, Promise<ClientChannel> promise, Promise<Void> closePromise, Consumer<WebSocketFrame> frameConsumer) {
		super(channel,promise);
		this.channel=channel;
		this.promise=promise;
		this.closePromise=closePromise;
		this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(URI.create("ws://"+channel.address().toString()+uri), WebSocketVersion.V13, null, false, null, 1280000);
		this.frameConsumer=frameConsumer;
	}
	
	@Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
		if(ctx.channel().isActive()) {
			handshaker.handshake(ctx.channel());
		} else {
			channelInactive(ctx);
		}
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!handshaker.isHandshakeComplete()) {
			try{
				handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
				promise.setSuccess(channel);
			} catch (WebSocketClientHandshakeException cause) {
				exceptionCaught(ctx, cause);
			}
		} else if(msg instanceof CloseWebSocketFrame){
			ctx.close().addListener(f->closePromise.setSuccess(null));
		} else {
			frameConsumer.accept((WebSocketFrame) msg);
		}
		ReferenceCountUtil.release(msg);
	}
	
}
