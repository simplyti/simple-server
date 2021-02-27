package com.simplyti.service.clients.stream;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpRequest;

public interface ClientRequestProvider {

	public HttpRequest request(boolean expectedContinue, ByteBuf buff);

}
