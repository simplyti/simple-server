package com.simplyti.service.clients.http.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Promise;

public class StreamedUnsafeTypeResponseHandler extends AbstractStreamedResponseHandler<Object> {

	public StreamedUnsafeTypeResponseHandler(ClientChannel channel, ByteBuf content, Promise<Void> promise, Consumer<Object> consumer) {
		super(channel, content, promise, consumer);
	}


}
