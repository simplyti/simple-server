package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleImmutableEntry;

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

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;

@Priority(1)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApiRequestHandlerInit extends HandlerInit {
	
	private final ApiResolver managerApiResolver;
	
	private final ApiResponseEncoder apiResponseEncoder;
	private final ApiInvocationHandler apiInvocationHandler;
	
	private final ExceptionHandler exceptionHandler;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private final ServerHeadersHandler serverHeadersHandler;
	
	private Deque<Entry<String, ChannelHandler>> handlers(ApiMacher apiMacher) {
		if(apiMacher.operation().isStreamed()) {
			Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
			handlers.add(new SimpleImmutableEntry<>("server-headers",serverHeadersHandler));
			handlers.add(new SimpleImmutableEntry<>("api-encoder",apiResponseEncoder));
			handlers.add(new SimpleImmutableEntry<>("chunk-write", new ChunkedWriteHandler()));
			handlers.add(new SimpleImmutableEntry<>("api-handler",new StreamedApiInvocationHandler(apiMacher,exceptionHandler,syncTaskSubmitter)));
			return handlers;
		}else {
			Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
			handlers.add(new SimpleImmutableEntry<>("aggregator", new HttpObjectAggregator(apiMacher.operation().maxBodyLength())));
			handlers.add(new SimpleImmutableEntry<>("server-headers",serverHeadersHandler));
			handlers.add(new SimpleImmutableEntry<>("api-encoder",apiResponseEncoder));
			handlers.add(new SimpleImmutableEntry<>("api-decoder",new ApiInvocationDecoder(apiMacher)));
			handlers.add(new SimpleImmutableEntry<>("chunk-write", new ChunkedWriteHandler()));
			handlers.add(new SimpleImmutableEntry<>("api-handler",apiInvocationHandler));
			return handlers;
		}
	}

	@Override
	protected Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
		Optional<ApiMacher> operation = managerApiResolver.getOperationFor(request.method(),queryStringDecoder);
		if(operation.isPresent()) {
			return handlers(operation.get());
		}else {
			return null;
		}
	}

}
