package com.simplyti.service.channel.handler.inits;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.simplyti.service.channel.handler.FileServeHandler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileServerHandlerInit extends HandlerInit{
	
	private final Optional<FileServeHandler> fileServeHandler;

	private Deque<Entry<String, ChannelHandler>> handlers() {
		Deque<Entry<String, ChannelHandler>> handlers = new LinkedList<>();
		fileServeHandler.ifPresent(fileServer->{
			handlers.add(Maps.immutableEntry("chunk-write", new ChunkedWriteHandler()));
			handlers.add(Maps.immutableEntry("file-server",fileServer));
		});
		return handlers;
	}

	protected Deque<Entry<String, ChannelHandler>> canHandle0(HttpRequest request) {
		if(fileServeHandler.isPresent() && fileServeHandler.get().matchRequest(request)) {
			return handlers();
		}else {
			return null;
		}
	}

}
