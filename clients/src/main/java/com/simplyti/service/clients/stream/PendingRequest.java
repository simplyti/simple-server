package com.simplyti.service.clients.stream;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.util.concurrent.Future;

public interface PendingRequest {

	Future<ClientChannel> send();

}
