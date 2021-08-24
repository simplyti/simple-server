package com.simplyti.server.http.api.handler.init;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.server.http.api.handler.ApiFullRequestAggregator;
import com.simplyti.server.http.api.handler.ApiInvocationHandler;
import com.simplyti.server.http.api.handler.ApiRequestDecoder;
import com.simplyti.server.http.api.handler.ApiResponseEncoder;
import com.simplyti.server.http.api.handler.OperationFilterHandler;
import com.simplyti.server.http.api.handler.StreamInitialApiInvocationHandler;
import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.config.ServerConfig;
import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.filter.priority.Priorized;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.server.http.api.filter.OperationInboundFilter;

import io.netty.channel.ChannelPipeline;

@Priority(1)
public class ApiHandlerInit implements ServiceHadlerInit {

	private final ServerConfig config;
	private final ApiRequestDecoder apiRequestDecoder;
	private final ApiResponseEncoder apiResponseEncoder;
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final Collection<OperationInboundFilter> filters;
	
	@Inject
	public ApiHandlerInit(ServerConfig config, ApiRequestDecoder apiRequestDecoder, ApiResponseEncoder apiResponseEncoder,
			ExceptionHandler exceptionHandler, SyncTaskSubmitter syncTaskSubmitter, Set<OperationInboundFilter> filters) {
		this.config=config;
		this.apiRequestDecoder=apiRequestDecoder;
		this.apiResponseEncoder=apiResponseEncoder;
		this.exceptionHandler=exceptionHandler;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.filters=filters.stream().sorted(Priorized.PRIORITY_ANN_ORDER).collect(Collectors.toList());
	}
	
	@Override
	public void init(ChannelPipeline pipeline) {
		pipeline.addBefore("default-handler", "api-res-encoder", apiResponseEncoder);
		pipeline.addBefore("default-handler", "api-req-decoder", apiRequestDecoder);
		if(!filters.isEmpty()) {
			pipeline.addBefore("default-handler", "api-filter-handler", new OperationFilterHandler(filters));
		}
		pipeline.addBefore("default-handler", "api-multipart-decoder", new MultipartApiHandler(exceptionHandler,syncTaskSubmitter));
		pipeline.addBefore("default-handler", "api-streamed-req-handler", new StreamInitialApiInvocationHandler(exceptionHandler,syncTaskSubmitter));
		pipeline.addBefore("default-handler", "api-aggregator", new ApiFullRequestAggregator(config));
		pipeline.addBefore("default-handler", "api-handler", new ApiInvocationHandler(syncTaskSubmitter, exceptionHandler));
	}

}
