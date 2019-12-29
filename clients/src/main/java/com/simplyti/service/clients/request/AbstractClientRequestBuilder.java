package com.simplyti.service.clients.request;


import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.util.concurrent.Future;

public abstract class AbstractClientRequestBuilder<T> implements BaseClientRequestBuilder<T>, ChannelProvider {
	
	private final ClientChannelFactory clientChannelFactory;
	
	private Endpoint endpoint;
	private long responseTimeoutMillis;
	private long readTimeoutMillis;

	public AbstractClientRequestBuilder(ClientChannelFactory clientChannelFactory, Endpoint endpoint) {
		this.clientChannelFactory=clientChannelFactory;
		this.endpoint=endpoint;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T withEndpoint(String host, int port) {
		this.endpoint = new Endpoint(null,host,port);
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withResponseTimeout(long timeoutMillis) {
		this.responseTimeoutMillis = timeoutMillis;
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withReadTimeout(long timeoutMillis) {
		this.readTimeoutMillis = timeoutMillis;
		return (T) this;
	}
	
	public Future<ClientChannel> channel() {
		return clientChannelFactory.channel(endpoint,responseTimeoutMillis,readTimeoutMillis);
	}
	

}
