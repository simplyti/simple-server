package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.InternalClient;

import io.netty.handler.codec.http.FullHttpRequest;

public class DefaultFinishableHttpRequest extends AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	private final FullHttpRequest request;

	public DefaultFinishableHttpRequest(InternalClient client, Endpoint endpoint, boolean checkStatusCode, FullHttpRequest request,
			long readTimeout) {
		super(client,endpoint,checkStatusCode,readTimeout);
		this.request = request;
	}

	@Override
	protected FullHttpRequest request() {
		return request;
	}

}
