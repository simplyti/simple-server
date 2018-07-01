package com.simplyti.service.clients.http.request;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

public interface StreamedHttpRequest{
	
	Future<Void> send(Object request);

	boolean isDone();

	boolean isSuccess();

	Future<Void> future();

	Future<Channel> channelFuture();

}
