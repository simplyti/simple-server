package com.simplyti.service.channel.handler.inits;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.channel.handler.ServerHeadersHandler;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;

@Priority(0)
public class FileServerHandlerInit extends HandlerInit{
	
	private final ChannelHandlerEntry fileServeHandler;
	private final ChannelHandlerEntry serverHeadersHandler;
	private final ServerConfig config;
	
	@Inject
	public FileServerHandlerInit(FileServeHandler fileServeHandler,ServerHeadersHandler serverHeadersHandler,
			ServerConfig config) {
		this.config=config;
		this.serverHeadersHandler=new ChannelHandlerEntry("server-headers",serverHeadersHandler);
		this.fileServeHandler=new ChannelHandlerEntry("file-server",fileServeHandler);
	}

	private ChannelHandlerEntry[] handlers() {
		return new ChannelHandlerEntry[] {
				new ChannelHandlerEntry("chunk-write", new ChunkedWriteHandler()),
				serverHeadersHandler,
				fileServeHandler
		};
	}

	protected ChannelHandlerEntry[] canHandle0(HttpRequest request) {
		if(config.fileServer().pattern().matcher(request.uri()).matches()) {
			return handlers();
		}else {
			return null;
		}
	}

}
