package com.simplyti.service.clients.stream;

import com.simplyti.service.clients.channel.ClientChannel;

import io.netty.handler.codec.http.HttpRequest;

public interface ClientRequestProvider {

	HttpRequest request(ClientChannel channel);

}
