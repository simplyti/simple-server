package com.simplyti.service.clients.http.request.stream;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.stream.PendingRequest;
import com.simplyti.service.commons.netty.pending.PendingMessages;
import com.simplyti.util.concurrent.Future;

public class DefaultStreamedBody implements StreamedBody {

	private final PendingRequest pendingRequest;
	private final Consumer<ClientChannel> initializer;
	private final PendingMessages pendingMessages;
	
	private Future<Object> channel;

	public DefaultStreamedBody(PendingRequest pendingRequest, Consumer<ClientChannel> initializer) {
		this.pendingRequest=pendingRequest;
		this.initializer=initializer;
		this.pendingMessages = new PendingMessages();
	}

	@Override
	public void send(Object build) {
		this.channel = pendingRequest.channel().thenApply(ch->{
			initializer.accept(ch);
			return ch;
		});
	}

}
