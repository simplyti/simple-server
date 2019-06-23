package com.simplyti.service.channel.handler;

import java.util.function.Consumer;

import com.simplyti.service.api.ApiMacher;
import com.simplyti.service.api.DefaultStreamedApiInvocationContext;
import com.simplyti.service.api.StreamedApiInvocation;
import com.simplyti.service.api.StreamedApiInvocationContext;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Promise;

public class StreamedApiInvocationHandler extends SimpleChannelInboundHandler<HttpObject>{
	
	private final ApiMacher apiMacher;
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private Consumer<ByteBuf> consumer;
	private Promise<Void> promise;
	
	public StreamedApiInvocationHandler(ApiMacher apiMacher,ExceptionHandler exceptionHandler,SyncTaskSubmitter syncTaskSubmitter) {
		this.apiMacher=apiMacher;
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
	}

	@SuppressWarnings({ "unchecked"})
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if(msg instanceof HttpRequest) {
			serviceProceed(ctx,(HttpRequest) msg,(Consumer<StreamedApiInvocationContext<?>>) apiMacher.operation().handler());
		}
		
		if(msg instanceof HttpContent && consumer!=null) {
			consumer.accept(((HttpContent) msg).content());
		}
		
		if(msg instanceof LastHttpContent && promise!=null) {
			promise.setSuccess(null);
		}
		
	}

	private <O> void serviceProceed(ChannelHandlerContext ctx, HttpRequest msg, Consumer<StreamedApiInvocationContext<?>> consumer) {
		StreamedApiInvocationContext<?> context = context(ctx,msg);
		try{
			consumer.accept(context);
		}catch(Throwable e) {
			//context.tryRelease();
			throw e;
		}
	}
	
	private <I,O> StreamedApiInvocationContext<O> context(ChannelHandlerContext ctx, HttpRequest msg) {
		return new DefaultStreamedApiInvocationContext<O>(ctx,apiMacher,new StreamedApiInvocation(msg,apiMacher.operation()),exceptionHandler,syncTaskSubmitter,this);
	}

	public void setDataConsumer(Consumer<ByteBuf> consumer, Promise<Void> promise) {
		this.consumer=consumer;
		this.promise=promise;
		
	}
	
}
