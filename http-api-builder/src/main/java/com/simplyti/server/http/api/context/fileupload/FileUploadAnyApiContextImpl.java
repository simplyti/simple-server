package com.simplyti.server.http.api.context.fileupload;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.simplyti.server.http.api.context.AbstractApiContext;
import com.simplyti.server.http.api.handler.message.ApiResponse;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class FileUploadAnyApiContextImpl extends AbstractApiContext implements FileUploadAnyApiContext {
	
	private final ChannelHandlerContext ctx;
	private final ExceptionHandler exceptionHandler;
	private final boolean isKeepAlive;
	private final HttpPostMultipartRequestDecoder decoder;
	private final ByteBuf body;
	
	private boolean released;
	
	public FileUploadAnyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ByteBuf body, ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx.channel(), request, match);
		this.ctx=ctx;
		this.exceptionHandler=exceptionHandler;
		this.isKeepAlive=HttpUtil.isKeepAlive(request);
		this.body=body;
		this.decoder = new HttpPostMultipartRequestDecoder(request);
	}

	@Override
	public Future<Void> failure(Throwable cause) {
		return exceptionHandler.exceptionCaught(ctx, cause);
	}

	@Override
	public Future<Void> close() {
		return new DefaultFuture<>(ctx.close(),ctx.executor());
	}

	@Override
	public void release() {
		if(this.released) {
			return;
		}
		decoder.destroy();
		body.release();
		this.released=true;
	}

	@Override
	public Collection<com.simplyti.server.http.api.fileupload.FileUpload> body() {
		List<com.simplyti.server.http.api.fileupload.FileUpload> files = decoder.getBodyHttpDatas().stream()
			.filter(data->data.getHttpDataType().equals(HttpDataType.FileUpload))
			.map(FileUpload.class::cast)
			.map(data->new com.simplyti.server.http.api.fileupload.FileUpload(data.content(),data.getFilename()))
			.collect(Collectors.toList());
		return files;
	}

	@Override
	public Future<Void> writeAndFlush(String message) {
		release();
		try {
			ChannelFuture future = ctx.writeAndFlush(new ApiResponse(message, isKeepAlive, false))
					.addListener(this::writeListener);
			return new DefaultFuture<>(future,ctx.executor());
		} catch(RuntimeException cause) {
			return new DefaultFuture<>(ctx.channel().eventLoop().newFailedFuture(cause), ctx.executor());
		}
	}
	
	private void writeListener(io.netty.util.concurrent.Future<? super Void> future) {
		if(future.isSuccess()) {
			if(!isKeepAlive) {
				ctx.channel().close();
			}
		}
	}
	
	@Override
	public Future<Void> writeAndFlush(Object value) {
		return null;
	}

	@Override
	public Future<Void> send(String message) {
		return writeAndFlush(message);
	}

	@Override
	public Future<Void> send(Object value) {
		return null;
	}

}
