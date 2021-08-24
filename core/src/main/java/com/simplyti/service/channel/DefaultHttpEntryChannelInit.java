package com.simplyti.service.channel;

import javax.inject.Inject;

import com.simplyti.service.channel.handler.ServerHeadersHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultHttpEntryChannelInit implements EntryChannelInit {
	
	private final ServerHeadersHandler serverHeadersHandler;
	
	@Override
	public void init(ChannelPipeline pipeline, boolean isSsl) {
		final HttpServerCodec serverCodec = new HttpServerCodec();
		pipeline.addLast(serverCodec);
		pipeline.addLast(serverHeadersHandler);
		pipeline.addLast(new HttpContentDecompressor());
	}

}
