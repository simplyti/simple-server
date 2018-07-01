package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.collect.Maps;
import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultBackendHandlerInit extends HandlerInit{
	
	private final Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler;
	
	private final Optional<Provider<DefaultBackendRequestHandler>> defaultBackendRequestHandler;
	
	private Deque<Entry<String, ChannelHandler>> fullHandlers(DefaultBackendFullRequestHandler handler) {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(Maps.immutableEntry("aggregator", new HttpObjectAggregator(10000000)));
		handlers.add(Maps.immutableEntry("default-notfound-full", handler));
		return handlers;
	}
	
	private Deque<Entry<String, ChannelHandler>> handlers(DefaultBackendRequestHandler handler) {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(Maps.immutableEntry("default-notfound", handler));
		return handlers;
	}

	@Override
	protected Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request) {
		if(defaultBackendRequestHandler.isPresent()) {
			return handlers(defaultBackendRequestHandler.get().get());
		} else if(defaultBackendFullRequestHandler.isPresent()) {
			return fullHandlers(defaultBackendFullRequestHandler.get());
		}else {
			return null;
		}
	}

}
