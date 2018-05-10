package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.simplyti.service.api.ApiMacher;
import com.simplyti.service.api.ApiResolver;
import com.simplyti.service.channel.handler.ApiInvocationDecoder;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApiRequestHandlerInit extends HandlerInit {
	
	private final ApiResolver managerApiResolver;
	
	private final ApiResponseEncoder apiResponseEncoder;
	private final ApiInvocationHandler apiInvocationHandler;
	
	private Deque<Entry<String, ChannelHandler>> handlers(ApiMacher apiMacher) {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(Maps.immutableEntry("aggregator", new HttpObjectAggregator(apiMacher.operation().maxBodyLength())));
		handlers.add(Maps.immutableEntry("api-encoder",apiResponseEncoder));
		handlers.add(Maps.immutableEntry("api-decoder",new ApiInvocationDecoder(apiMacher)));
		handlers.add(Maps.immutableEntry("api-handler",apiInvocationHandler));
		return handlers;
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
