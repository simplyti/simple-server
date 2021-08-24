package com.simplyti.server.http.api.context.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


public class WebSocketApiContextImpl extends SimpleChannelInboundHandler<WebSocketFrame> implements WebSocketApiContext {

	private final ChannelHandlerContext ctx;
	private final List<Consumer<String>> stringConsumers;
	private final List<Consumer<ByteBuf>> bytebufConsumers;

	public WebSocketApiContextImpl(ChannelHandlerContext ctx) {
		this.ctx=ctx;
		this.stringConsumers=new ArrayList<>();
		this.bytebufConsumers=new ArrayList<>();
	}

	@Override
	public Future<Void> send(String data) {
		ChannelFuture future =  ctx.writeAndFlush(new TextWebSocketFrame(data));
		return new DefaultFuture<>(future,ctx.executor());
	}
	
	@Override
	public Future<Void> send(ByteBuf data) {
		ChannelFuture future =  ctx.writeAndFlush(new BinaryWebSocketFrame(data));
		return new DefaultFuture<>(future,ctx.executor());
	}
	
	@Override
	public Future<Void> close() {
		return close0().thenCombine(f->ctx.close());
	}

	private Future<Void> close0() {
		return new DefaultFuture<>(ctx.writeAndFlush(new CloseWebSocketFrame()),ctx.executor());
	}

	@Override
	public void onMessage(StringConsumer consumer) {
		this.stringConsumers.add(consumer);
	}
	
	@Override
	public void onMessage(ByteBufConsumer consumer) {
		this.bytebufConsumers.add(consumer);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		if(msg instanceof TextWebSocketFrame) {
			String text = ((TextWebSocketFrame) msg).text();
			this.stringConsumers.forEach(f->f.accept(text));
			this.bytebufConsumers.forEach(f->f.accept(((TextWebSocketFrame) msg).content()));
		} else if(msg instanceof BinaryWebSocketFrame) {
			ByteBuf content = ((BinaryWebSocketFrame) msg).content();
			this.bytebufConsumers.forEach(f->f.accept(content));
		} else if (msg instanceof CloseWebSocketFrame){
			ctx.close();
		}
	}

}
