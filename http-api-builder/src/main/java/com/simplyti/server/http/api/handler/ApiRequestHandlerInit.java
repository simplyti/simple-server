package com.simplyti.server.http.api.handler;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.server.http.api.operations.ApiOperationResolver;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.inits.ChannelHandlerEntry;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.AllArgsConstructor;

@Priority(1)
@AllArgsConstructor(onConstructor=@__(@Inject))
public class ApiRequestHandlerInit extends HandlerInit {
	
	private final ApiOperationResolver apiResolver;
	private final ApiResponseEncoder apiResponseEncoder;
	private final ApiInvocationHandler apiInvocationHandler;
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final ServerHeadersHandler serverHeadersHandler;
	
	@Override
	protected ChannelHandlerEntry[] canHandle0(HttpRequest request) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
		ApiMatchRequest matchRequest = apiResolver.resolveOperation(request.method(),queryStringDecoder.path(),queryStringDecoder);
		if(matchRequest==null) {
			return null;
		}
		return new DefaultApiRequestInitializer(matchRequest, apiResponseEncoder, apiInvocationHandler,exceptionHandler,syncTaskSubmitter,serverHeadersHandler).handlers();
	}

}
