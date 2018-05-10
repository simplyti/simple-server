package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultFullRequestBackendHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultBackendHandlerInit extends HandlerInit{
	
	private final Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler;
	
	private final DefaultFullRequestBackendHandler defaultFullRequestBackendHandler;

	private Deque<Entry<String, ChannelHandler>> handlers() {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(Maps.immutableEntry("aggregator", new HttpObjectAggregator(10000000)));
		handlers.add(Maps.immutableEntry("default-notfound-full", defaultFullRequestBackendHandler));
		return handlers;
	}

	@Override
	protected Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request) {
		if(defaultBackendFullRequestHandler.isPresent()) {
			return handlers();
		}else {
			return null;
		}
	}

}
