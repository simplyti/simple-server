package com.simplyti.server.http.api.handler.init;

import java.util.Set;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.server.http.api.handler.ApiFullRequestAggregator;
import com.simplyti.server.http.api.handler.ApiInvocationHandler;
import com.simplyti.server.http.api.handler.ApiRequestDecoder;
import com.simplyti.server.http.api.handler.ApiResponseEncoder;
import com.simplyti.server.http.api.handler.StreamInitialApiInvocationHandler;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.server.http.api.filter.OperationInboundFilter;

import io.netty.channel.ChannelPipeline;
import lombok.AllArgsConstructor;

@Priority(1)
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ApiHandlerInit implements ServiceHadlerInit {

	private final ServerConfig config;
	private final ApiRequestDecoder apiRequestDecoder;
	private final ApiResponseEncoder apiResponseEncoder;
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final Set<OperationInboundFilter> filters;
	
	@Override
	public void init(ChannelPipeline pipeline) {
		pipeline.addBefore("default-handler", "api-res-encoder", apiResponseEncoder);
		pipeline.addBefore("default-handler", "api-req-decoder", apiRequestDecoder);
		pipeline.addBefore("default-handler", "api-multipart-decoder", new MultipartApiHandler(exceptionHandler,syncTaskSubmitter));
		pipeline.addBefore("default-handler", "api-streamed-req-handler", new StreamInitialApiInvocationHandler(exceptionHandler,syncTaskSubmitter));
		pipeline.addBefore("default-handler", "api-aggregator", new ApiFullRequestAggregator(config));
		pipeline.addBefore("default-handler", "api-handler", new ApiInvocationHandler(syncTaskSubmitter, exceptionHandler, filters));
	}

}
