package com.simplyti.service.clients.http.request;

import java.util.function.Consumer;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.sse.domain.ServerEvent;
import com.simplyti.util.concurrent.Future;

public interface ServerSentEventRequestBuilder {

	Future<Void> forEach(Consumer<ServerEvent> consumer);

	ServerSentEventRequestBuilder onConnect(Consumer<ClientChannel> consumer);

}
