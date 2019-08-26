package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultBackendHandlerInit extends HandlerInit{
	
	private final Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler;
	
	private final Provider<Optional<DefaultBackendRequestHandler>> defaultBackendRequestHandler;
	
	private Deque<Entry<String, ChannelHandler>> fullHandlers(DefaultBackendFullRequestHandler handler) {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(new SimpleImmutableEntry<>("aggregator", new HttpObjectAggregator(10000000)));
		handlers.add(new SimpleImmutableEntry<>("default-notfound-full", handler));
		return handlers;
	}
	
	private Deque<Entry<String, ChannelHandler>> handlers(DefaultBackendRequestHandler handler) {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(new SimpleImmutableEntry<>("default-notfound", handler));
		return handlers;
	}

	@Override
	protected Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request) {
		Optional<DefaultBackendRequestHandler> backendRequestHandler = defaultBackendRequestHandler.get();
		if(backendRequestHandler.isPresent()) {
			return handlers(backendRequestHandler.get());
		} else if(defaultBackendFullRequestHandler.isPresent()) {
			return fullHandlers(defaultBackendFullRequestHandler.get());
		}else {
			return null;
		}
	}

}
