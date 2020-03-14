package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.AnyApiContextFactory;
import com.simplyti.server.http.api.context.AnyWithBodyApiContextFactory;
import com.simplyti.server.http.api.context.ApiContextFactory;
import com.simplyti.server.http.api.context.RequestResponseTypedContextFactory;
import com.simplyti.server.http.api.context.RequestTypedContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedApiContextFactory;
import com.simplyti.server.http.api.context.ResponseTypedWithBodyContextFactory;
import com.simplyti.server.http.api.context.fileupload.FileUploadContextFactory;
import com.simplyti.server.http.api.context.sse.ServerSentEventContextFactory;
import com.simplyti.server.http.api.context.stream.StreamAnyContextFactory;
import com.simplyti.server.http.api.operations.ApiOperations;
import com.simplyti.service.api.serializer.json.Json;

import io.netty.handler.codec.http.HttpMethod;

public class HttpMethodApiBuilderImpl implements HttpMethodApiBuilder {
	
	private final ApiOperations operations;
	private final ApiContextFactory anyContextFactory;
	private final ApiContextFactory responseTypeContextFactory;
	private final ApiContextFactory anyWithBodyContextFactory;
	private final ApiContextFactory requestTypedContextFactory;
	private final ApiContextFactory responseTypedWithBodyContextFactory;
	private final ApiContextFactory requestResponseTypedContextFactory;
	private final ApiContextFactory streamAnyContextFactory;
	private final ApiContextFactory fileUploadContextFactory;
	private final ApiContextFactory serverSentEventContextFactory;
	

	public HttpMethodApiBuilderImpl(ApiOperations operations, Json json) {
		this.operations=operations;
		this.anyContextFactory=new AnyApiContextFactory();
		this.responseTypeContextFactory=new ResponseTypedApiContextFactory();
		this.anyWithBodyContextFactory=new AnyWithBodyApiContextFactory();
		this.requestTypedContextFactory=new RequestTypedContextFactory(json);
		this.responseTypedWithBodyContextFactory = new ResponseTypedWithBodyContextFactory();
		this.requestResponseTypedContextFactory=new RequestResponseTypedContextFactory(json);
		this.streamAnyContextFactory= new StreamAnyContextFactory();
		this.fileUploadContextFactory = new FileUploadContextFactory();
		this.serverSentEventContextFactory = new ServerSentEventContextFactory();
	}

	@Override
	public ResponseTypableApiBuilder get(String path) {
		return new ResponseBodyTypableApiBuilderImpl(anyContextFactory,responseTypeContextFactory, serverSentEventContextFactory, operations,HttpMethod.GET, path);
	}
	
	@Override
	public ResponseTypableApiBuilder delete(String path) {
		return new ResponseBodyTypableApiBuilderImpl(anyContextFactory,responseTypeContextFactory, serverSentEventContextFactory, operations,HttpMethod.DELETE, path);
	}

	@Override
	public RequestTypableApiBuilder post(String path) {
		return new RequestBodyTypableApiBuilderImpl(anyWithBodyContextFactory,requestTypedContextFactory,requestResponseTypedContextFactory,responseTypedWithBodyContextFactory,streamAnyContextFactory,fileUploadContextFactory,operations,HttpMethod.POST, path);
	}

	@Override
	public RequestTypableApiBuilder put(String path) {
		return new RequestBodyTypableApiBuilderImpl(anyWithBodyContextFactory,requestTypedContextFactory,requestResponseTypedContextFactory,responseTypedWithBodyContextFactory,streamAnyContextFactory,fileUploadContextFactory,operations,HttpMethod.PUT, path);
	}

	@Override
	public RequestTypableApiBuilder patch(String path) {
		return new RequestBodyTypableApiBuilderImpl(anyWithBodyContextFactory,requestTypedContextFactory,requestResponseTypedContextFactory,responseTypedWithBodyContextFactory,streamAnyContextFactory,fileUploadContextFactory,operations,HttpMethod.PATCH, path);
	}

}
