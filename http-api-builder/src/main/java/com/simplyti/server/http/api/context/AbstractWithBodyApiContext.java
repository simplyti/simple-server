package com.simplyti.server.http.api.context;


import java.io.Closeable;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import lombok.SneakyThrows;

public abstract class AbstractWithBodyApiContext<T> extends  AbstractApiContext<T> implements WithBodyApiContext {

	private final Closeable body;
	private boolean released;

	public AbstractWithBodyApiContext(SyncTaskSubmitter syncTaskSubmitter, ChannelHandlerContext ctx, HttpRequest request, ApiMatchRequest matcher, 
			ExceptionHandler exceptionHandler, Closeable body) {
		super(syncTaskSubmitter, ctx, request, matcher, exceptionHandler);
		this.body=body;
	}
	
	public Future<Void> writeAndFlush(ByteBuf value) {
		release();
		return super.writeAndFlush(value);
	}
	
	public Future<Void> writeAndFlush(String message) {
		release();
		return super.writeAndFlush(message);
	}
	
	public Future<Void> writeAndFlush(T value) {
		release();
		return super.writeAndFlush(value);
	}
	
	public Future<Void> writeAndFlush(HttpObject response) {
		release();
		return super.writeAndFlush(response);
	}
	
	public Future<Void> writeAndFlushEmpty() {
		release();
		return super.writeAndFlushEmpty();
	}
	
	@Override
	public Future<Void> failure(Throwable cause) {
		release();
		return super.failure(cause);
	}
	
	@Override
	public Future<Void> close() {
		release();
		return super.close();
	}
	
	@Override
	public void release() {
		if(this.released) {
			return;
		}
		release0();
		this.released=true;
	}
	
	@SneakyThrows
	private void release0() {
		body.close();
	}

	public Future<Void> send(T body) {
		return writeAndFlush(body);
	}

	public Future<Void> send(String message) {
		return writeAndFlush(message);
	}

	public Future<Void> send(ByteBuf body) {
		return writeAndFlush(body);
	}
	
	public Future<Void> send(HttpObject response) {
		return writeAndFlush(response);
	}
	
	public Future<Void> sendEmpty() {
		return writeAndFlushEmpty();
	}
	
}
