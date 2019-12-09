package com.simplyti.service.channel.handler.inits;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.fileserver.FileServeConfiguration;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.stream.ChunkedWriteHandler;

@Priority(0)
public class FileServerHandlerInit extends HandlerInit{
	
	private final ChannelHandlerEntry fileServeHandler;
	private final ChannelHandlerEntry serverHeadersHandler;
	private final FileServeConfiguration fileServerConfig;
	
	@Inject
	public FileServerHandlerInit(FileServeHandler fileServeHandler,ServerHeadersHandler serverHeadersHandler,
			FileServeConfiguration fileServerConfig) {
		this.fileServerConfig=fileServerConfig;
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
		if(fileServerConfig.pattern().matcher(request.uri()).matches()) {
			return handlers();
		}else {
			return null;
		}
	}

}
