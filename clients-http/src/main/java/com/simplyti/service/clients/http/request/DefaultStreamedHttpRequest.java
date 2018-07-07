package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.stream.StreamedClient;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.concurrent.Future;

public class DefaultStreamedHttpRequest extends StreamedClient<HttpContent> implements StreamedHttpRequest {

	public DefaultStreamedHttpRequest(Future<ClientRequestChannel<Void>> clientChannel, EventLoop executor) {
		super(clientChannel, executor);
	}

}
