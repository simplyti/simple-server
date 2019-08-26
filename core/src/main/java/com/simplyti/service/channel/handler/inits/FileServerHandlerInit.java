package com.simplyti.service.channel.handler.inits;

import java.util.AbstractMap;
import java.util.Deque;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.channel.handler.ServerHeadersHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;

@Priority(0)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileServerHandlerInit extends HandlerInit{
	
	private final FileServeHandler fileServeHandler;
	private final ServerHeadersHandler serverHeadersHandler;

	private Deque<Entry<String, ChannelHandler>> handlers() {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(new AbstractMap.SimpleEntry<>("chunk-write", new ChunkedWriteHandler()));
		handlers.add(new SimpleImmutableEntry<>("server-headers",serverHeadersHandler));
		handlers.add(new AbstractMap.SimpleEntry<>("file-server",fileServeHandler));
		return handlers;
	}

	protected Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request) {
		if(fileServeHandler.matchRequest(request)) {
			return handlers();
		}else {
			return null;
		}
	}

}
