package com.simplyti.service.builder.di.guice;

import com.simplyti.service.channel.EntryChannelInit;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

public class DefaultHttpEntryChannelInit implements EntryChannelInit {

	@Override
	public void init(ChannelPipeline pipeline) {
		pipeline.addLast(new HttpServerCodec());
	}

}
