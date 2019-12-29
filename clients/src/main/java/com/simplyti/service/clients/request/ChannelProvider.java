package com.simplyti.service.clients.request;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.util.concurrent.Future;

public interface ChannelProvider {

	Future<ClientChannel> channel();

}
