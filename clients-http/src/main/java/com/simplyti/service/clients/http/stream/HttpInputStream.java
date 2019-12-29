package com.simplyti.service.clients.http.stream;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.stream.InputDataStream;
import com.simplyti.util.concurrent.Future;

import io.netty.handler.codec.http.HttpObject;

public interface HttpInputStream extends InputDataStream {
	
	Future<Void> forEach(Consumer<HttpObject> consumer);
	
	Future<Void> withHandler(Consumer<ClientChannel> initialized);

}
