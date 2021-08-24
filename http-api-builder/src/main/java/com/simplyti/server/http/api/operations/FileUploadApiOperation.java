package com.simplyti.server.http.api.operations;

import java.util.Map;

import com.simplyti.server.http.api.builder.fileupload.FileUploadAnyApiContextConsumer;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.fileupload.FileUploadAnyApiContext;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class FileUploadApiOperation extends ApiOperation<FileUploadAnyApiContext> {
	
	public FileUploadApiOperation(HttpMethod method, ApiPattern pattern, Map<String,Object> metadata, FileUploadAnyApiContextConsumer consumer, ApiContextFactory contextFactory,
			boolean notFoundOnNull) {
		super(method, pattern,metadata, consumer,contextFactory, false, notFoundOnNull);
	}

}
