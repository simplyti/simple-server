package com.simplyti.service.clients.http.handler;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Promise;

public class StreamedByteBufResponseHandler extends AbstractStreamedResponseHandler<ByteBuf> {

	public StreamedByteBufResponseHandler(ClientChannel channel, Promise<Void> promise, Consumer<ByteBuf> consumer) {
		super(channel, promise, consumer);
	}

}
