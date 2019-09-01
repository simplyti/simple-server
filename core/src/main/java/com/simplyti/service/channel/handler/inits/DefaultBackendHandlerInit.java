package com.simplyti.service.channel.handler.inits;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.channel.handler.DefaultBackendFullRequestHandler;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultBackendHandlerInit extends HandlerInit{
	
	private final Optional<DefaultBackendFullRequestHandler> defaultBackendFullRequestHandler;
	
	private final Provider<Optional<DefaultBackendRequestHandler>> defaultBackendRequestHandler;
	
	private ChannelHandlerEntry[] fullHandlers(DefaultBackendFullRequestHandler handler) {
		return new ChannelHandlerEntry[]{ 
				new ChannelHandlerEntry("aggregator", new HttpObjectAggregator(10000000)),
				new ChannelHandlerEntry("default-notfound-full", handler)};
	}
	
	private ChannelHandlerEntry[] handlers(DefaultBackendRequestHandler handler) {
		return new ChannelHandlerEntry[]{ 
				new ChannelHandlerEntry("default-notfound", handler)};
	}

	@Override
	protected ChannelHandlerEntry[] canHandle0(HttpRequest request) {
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
