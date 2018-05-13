package com.simplyti.service.aws.lambda;

import java.io.IOException;
import java.io.InputStream;

import com.simplyti.service.channel.ServiceChannelInitializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.embedded.EmbeddedChannel;

public class LambdaChannel extends EmbeddedChannel{

	private byte[] inputBuffer;

	public LambdaChannel(ServiceChannelInitializer channelInitHandler) {
		super(DefaultChannelId.newInstance(),channelInitHandler);
		this.inputBuffer = new byte[1024];
	}

	public void read(InputStream inputStream) throws IOException {
		int nRead;
		while ((nRead = inputStream.read(inputBuffer, 0, inputBuffer.length)) != -1) {
			ByteBuf buff = alloc().buffer(nRead).writeBytes(inputBuffer, 0, nRead);
			writeOneInbound(buff);
		}
	}

}
