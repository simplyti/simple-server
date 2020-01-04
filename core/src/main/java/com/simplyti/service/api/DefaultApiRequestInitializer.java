package com.simplyti.service.api;

import javax.inject.Inject;

import com.simplyti.service.channel.handler.ApiInvocationDecoder;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.StreamedApiInvocationHandler;
import com.simplyti.service.channel.handler.inits.ChannelHandlerEntry;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;

public class DefaultApiRequestInitializer implements ApiRequestInitializer {

	private final ApiMacher apiMacher;
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private final ChannelHandlerEntry apiResponseEncoder;
	private final ChannelHandlerEntry serverHeadersHandler;
	private final ChannelHandlerEntry apiInvocationHandler;
	
	@Inject
	public DefaultApiRequestInitializer(ApiMacher apiMacher,ApiResponseEncoder apiResponseEncoder,
			ApiInvocationHandler apiInvocationHandler, ExceptionHandler exceptionHandler,
			SyncTaskSubmitter syncTaskSubmitter, ServerHeadersHandler serverHeadersHandler) {
		this.apiMacher=apiMacher;
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.serverHeadersHandler=new ChannelHandlerEntry("server-headers",serverHeadersHandler);
		this.apiResponseEncoder=new ChannelHandlerEntry("api-encoder",apiResponseEncoder);
		this.apiInvocationHandler=new ChannelHandlerEntry("api-handler",apiInvocationHandler);
	}
	

	@Override
	public ChannelHandlerEntry[] handlers() {
		if(apiMacher.operation().isStreamed()) {
			return new ChannelHandlerEntry[] {
					this.serverHeadersHandler,
					this.apiResponseEncoder,
					new ChannelHandlerEntry("chunk-write", new ChunkedWriteHandler()),
					new ChannelHandlerEntry("api-handler",new StreamedApiInvocationHandler(apiMacher,exceptionHandler,syncTaskSubmitter))
			};
		}else {
			return new ChannelHandlerEntry[] {
					new ChannelHandlerEntry("aggregator", new HttpObjectAggregator(apiMacher.operation().maxBodyLength())),
					this.serverHeadersHandler,
					this.apiResponseEncoder,
					new ChannelHandlerEntry("api-decoder",new ApiInvocationDecoder(apiMacher)),
					new ChannelHandlerEntry("chunk-write", new ChunkedWriteHandler()),
					this.apiInvocationHandler
			};
		}
	}

}
