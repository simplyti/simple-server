package com.simplyti.service.clients.channel;

import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.util.concurrent.Future;

public interface ClientChannelFactory {

	Future<ClientChannel> channel(Endpoint endpoint, long responseTimeoutMillis);

}
