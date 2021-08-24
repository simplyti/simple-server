package com.simplyti.server.http.api.builder.fileupload;

import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.server.http.api.operations.FileUploadApiOperation;
import com.simplyti.service.matcher.ApiPattern;

import io.netty.handler.codec.http.HttpMethod;

public class FileUploadApiBuilderImpl implements FileUploadApiBuilder {
	
	private final ApiOperations operations;
	private final HttpMethod method;
	private final String path;
	private final ApiContextFactory streamAnyContextFactory;

	public FileUploadApiBuilderImpl(ApiContextFactory streamContextFactory, ApiOperations operations, HttpMethod method, String path) {
		this.operations=operations;
		this.method=method;
		this.path=path;
		this.streamAnyContextFactory=streamContextFactory;
	}
	
	@Override
	public void then(FileUploadAnyApiContextConsumer consumer) {
		ApiPattern apiPattern = ApiPattern.build(path);
		operations.add(new FileUploadApiOperation(method,apiPattern,null,consumer,streamAnyContextFactory, false));
	}

}
