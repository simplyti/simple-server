package com.simplyti.server.http.api.context.fileupload;


import java.util.List;
import java.util.stream.Collectors;

import com.simplyti.server.http.api.context.AbstractWithBodyApiContext;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostMultipartRequestDecoder;

public class FileUploadAnyApiContextImpl extends AbstractWithBodyApiContext<Object> implements FileUploadAnyApiContext {
	
	private final HttpPostMultipartRequestDecoder decode;
	
	public FileUploadAnyApiContextImpl(SyncTaskSubmitter syncTaskSubmitter, ExceptionHandler exceptionHandler, ChannelHandlerContext ctx, HttpRequest request, ApiMatchRequest matcher, HttpPostMultipartRequestDecoder decode) {
		super(syncTaskSubmitter, ctx, request, matcher, exceptionHandler, ()->decode.destroy());
		this.decode=decode;
	}

	@Override
	public List<FileUpload> files() {
		return this.decode.getBodyHttpDatas()
			.stream().filter(f-> f instanceof FileUpload)
			.map(FileUpload.class::cast)
			.collect(Collectors.toList());
	}
	
}
