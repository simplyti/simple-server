package com.simplyti.server.http.api.handler;

import com.simplyti.util.concurrent.ThrowableConsumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.concurrent.Promise;

public class MultipartApiInvocationHandler extends SimpleChannelInboundHandler<HttpContent> {

	private final ThrowableConsumer<InterfaceHttpData> consumer;
	private final Promise<Void> promise;
	private final HttpPostMultipartRequestDecoder decode;
	
	private boolean failure;

	public MultipartApiInvocationHandler(HttpRequest request, ThrowableConsumer<InterfaceHttpData> consumer, Promise<Void> promise) {
		this.consumer=consumer;
		this.promise=promise;
		this.decode=new HttpPostMultipartRequestDecoder(new DefaultHttpDataFactory(true),request);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
		if(failure) {
			if(msg instanceof LastHttpContent) {
				ctx.pipeline().remove(this);
			}
			return;
		}
		
		
		if(msg.content().isReadable()) {
			decode.offer(msg);
			while(decode.hasNext()){
				InterfaceHttpData data = decode.next();
				handleFile(data);
			}
		}
		
		if(msg instanceof LastHttpContent) {
			decode.destroy();
			ctx.pipeline().remove(this);
			promise.setSuccess(null);
		}
	}

	private void handleFile(InterfaceHttpData data) {
		try{
			consumer.accept(data);
			decode.removeHttpDataFromClean(data);
		} catch (Throwable e) {
			this.failure = true;
			promise.setFailure(e);
			decode.destroy();
		}
	}

}
