package com.simplyti.service.clients.http.request;

import com.simplyti.service.clients.ClientConfig;
import com.simplyti.service.clients.InternalClient;

import io.netty.handler.codec.http.FullHttpRequest;

public class DefaultFinishableHttpRequest extends AbstractFinishableHttpRequest implements FinishableHttpRequest {
	
	private final FullHttpRequest request;

	public DefaultFinishableHttpRequest(InternalClient client, boolean checkStatusCode, FullHttpRequest request, ClientConfig config) {
		super(client,checkStatusCode,config);
		this.request = request;
	}

	@Override
	protected FullHttpRequest request0() {
		return request;
	}

}
