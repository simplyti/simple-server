package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.simplyti.service.channel.handler.FileServeHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;

@Priority(0)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileServerHandlerInit extends HandlerInit{
	
	private final FileServeHandler fileServeHandler;

	private Deque<Entry<String, ChannelHandler>> handlers() {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		handlers.add(Maps.immutableEntry("chunk-write", new ChunkedWriteHandler()));
		handlers.add(Maps.immutableEntry("file-server",fileServeHandler));
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
