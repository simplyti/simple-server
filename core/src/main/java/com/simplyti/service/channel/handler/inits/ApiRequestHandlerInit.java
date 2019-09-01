package com.simplyti.service.channel.handler.inits;

import java.util.Optional;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.api.ApiMacher;
import com.simplyti.service.api.ApiResolver;
import com.simplyti.service.channel.handler.ApiInvocationDecoder;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.StreamedApiInvocationHandler;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

@Priority(1)
public class ApiRequestHandlerInit extends HandlerInit {
	
	private final ApiResolver managerApiResolver;
	
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private final ChannelHandlerEntry apiResponseEncoder;
	private final ChannelHandlerEntry serverHeadersHandler;
	private final ChannelHandlerEntry apiInvocationHandler;
	
	@Inject
	public ApiRequestHandlerInit(ApiResolver managerApiResolver,ApiResponseEncoder apiResponseEncoder,
			ApiInvocationHandler apiInvocationHandler, ExceptionHandler exceptionHandler,
			SyncTaskSubmitter syncTaskSubmitter, ServerHeadersHandler serverHeadersHandler) {
		this.managerApiResolver=managerApiResolver;
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.serverHeadersHandler=new ChannelHandlerEntry("server-headers",serverHeadersHandler);
		this.apiResponseEncoder=new ChannelHandlerEntry("api-encoder",apiResponseEncoder);
		this.apiInvocationHandler=new ChannelHandlerEntry("api-handler",apiInvocationHandler);
	}
	
	private ChannelHandlerEntry[] handlers(ApiMacher apiMacher) {
		if(apiMacher.operation().isStreamed()) {
			return new ChannelHandlerEntry[] {
					this.serverHeadersHandler,
					apiResponseEncoder,
					new ChannelHandlerEntry("chunk-write", new ChunkedWriteHandler()),
					new ChannelHandlerEntry("api-handler",new StreamedApiInvocationHandler(apiMacher,exceptionHandler,syncTaskSubmitter))
			};
		}else {
			return new ChannelHandlerEntry[] {
					new ChannelHandlerEntry("aggregator", new HttpObjectAggregator(apiMacher.operation().maxBodyLength())),
					serverHeadersHandler,
					apiResponseEncoder,
					new ChannelHandlerEntry("api-decoder",new ApiInvocationDecoder(apiMacher)),
					new ChannelHandlerEntry("chunk-write", new ChunkedWriteHandler()),
					apiInvocationHandler
			};
		}
	}

	@Override
	protected ChannelHandlerEntry[] canHandle0(HttpRequest request) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
		Optional<ApiMacher> operation = managerApiResolver.getOperationFor(request.method(),queryStringDecoder);
		if(operation.isPresent()) {
			return handlers(operation.get());
		}else {
			return null;
		}
	}

}
