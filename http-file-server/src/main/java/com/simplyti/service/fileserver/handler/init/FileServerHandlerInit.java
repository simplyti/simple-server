package com.simplyti.service.fileserver.handler.init;

import javax.annotation.Priority;
import javax.inject.Inject;

import com.simplyti.service.channel.handler.inits.ServiceHadlerInit;
import com.simplyti.service.fileserver.handler.FileServeHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.stream.ChunkedWriteHandler;

@Priority(0)
public class FileServerHandlerInit implements ServiceHadlerInit{
	
	private final FileServeHandler fileServeHandler;
	
	@Inject
	public FileServerHandlerInit(FileServeHandler fileServeHandler) {
		this.fileServeHandler=fileServeHandler;
	}

	@Override
	public void init(ChannelPipeline pipeline) {
		pipeline.addBefore("default-handler", "chunk-write", new ChunkedWriteHandler());
		pipeline.addBefore("default-handler", "file-handler", fileServeHandler);
		
	}

}
