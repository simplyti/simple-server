package com.simplyti.service.clients.http.stream.handler;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Promise;

public abstract class AbstractStreamResponseHandler extends SimpleChannelInboundHandler<HttpObject> {

	private final Promise<Void> promise;
	private final ClientChannel channel;
	private final List<String> originalHandlers;
	private final boolean failOnClose;

	public AbstractStreamResponseHandler(ClientChannel channel, Promise<Void> promise, List<String> originalHandlers, boolean failOnClose) {
		this.promise=promise;
		this.channel=channel;
		this.originalHandlers=originalHandlers;
		this.failOnClose=failOnClose;
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if(failOnClose) {
			promise.tryFailure(new ClosedChannelException());
		} else {
			promise.trySuccess(null);
		}
		cleanChannel();
		channel.release();
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if(msg instanceof HttpResponse && isError(((HttpResponse) msg).status().codeClass())) {
			promise.setFailure(new HttpException(((HttpResponse) msg).status().code()));
		}
		
		if(!promise.isDone()) {
			handle(ctx,msg);
		}
		
		if(msg instanceof LastHttpContent) {
			promise.trySuccess(null);
			cleanChannel();
			channel.release();
		}
	}
	

	private void cleanChannel() {
		List<String> toRemove = StreamSupport.stream(channel.pipeline().spliterator(),false)
			.map(Entry::getKey)
			.filter(name->!originalHandlers.contains(name))
			.collect(Collectors.toList());
		toRemove.forEach(channel.pipeline()::remove);
	}

	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}

	protected abstract void handle(ChannelHandlerContext ctx, HttpObject msg);

}
