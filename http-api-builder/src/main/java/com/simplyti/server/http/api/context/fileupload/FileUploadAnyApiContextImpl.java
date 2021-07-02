package com.simplyti.server.http.api.context.fileupload;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.simplyti.server.http.api.context.AbstractWithBodyApiContext;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class FileUploadAnyApiContextImpl extends AbstractWithBodyApiContext<Object> implements FileUploadAnyApiContext {
	
	private final HttpPostMultipartRequestDecoder decoder;
	private final ByteBuf body;
	
	private boolean released;
	
	public FileUploadAnyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ByteBuf body, ApiMatchRequest match) {
		super(syncTaskSubmitter, ctx, request, match, exceptionHandler, body);
		this.body=body;
		this.decoder = new HttpPostMultipartRequestDecoder(request);
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
		List<InterfaceHttpData> parts = decoder.getBodyHttpDatas();
		List<com.simplyti.server.http.api.fileupload.FileUpload> files =parts.stream()
			.filter(data->data.getHttpDataType().equals(HttpDataType.FileUpload))
			.map(FileUpload.class::cast)
			.map(data->new com.simplyti.server.http.api.fileupload.FileUpload(data.getName(), data.content(), data.getFilename()))
			.collect(Collectors.toList());
		return files;
	}
	
}
