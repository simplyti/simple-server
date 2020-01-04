package com.simplyti.service.channel.handler.inits;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.api.ApiResolver;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

@Priority(1)
public class ApiRequestHandlerInit extends HandlerInit {
	
	private final ApiResolver managerApiResolver;
	
	@Inject
	public ApiRequestHandlerInit(ApiResolver managerApiResolver,ApiResponseEncoder apiResponseEncoder,
			ApiInvocationHandler apiInvocationHandler, ExceptionHandler exceptionHandler,
			SyncTaskSubmitter syncTaskSubmitter, ServerHeadersHandler serverHeadersHandler) {
		this.managerApiResolver=managerApiResolver;
	}
	
	@Override
	protected ChannelHandlerEntry[] canHandle0(HttpRequest request) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
		return managerApiResolver.getOperationFor(request.method(),queryStringDecoder).handlers();
	}

}
