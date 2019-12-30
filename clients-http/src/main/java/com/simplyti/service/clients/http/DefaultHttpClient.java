package com.simplyti.service.clients.http;

import com.simplyti.service.clients.channel.ClientChannelFactory;
import com.simplyti.service.clients.channel.FixedSizeClientChannelFactory;
import com.simplyti.service.clients.channel.SimpleClientChannelFactory;
import com.simplyti.service.clients.channel.UnpooledClientChannelFactory;
import com.simplyti.service.clients.channel.handler.IdleTimeoutHandler;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.request.DefaultHttpRequestBuilder;
import com.simplyti.service.clients.http.request.HttpRequestBuilder;
import com.simplyti.service.clients.monitor.ClientMonitor;
import com.simplyti.service.clients.monitor.DefaultClientMonitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.concurrent.Future;

public class DefaultHttpClient extends AbstractChannelPoolHandler implements HttpClient {

	private final ClientChannelFactory clientFactory;
	private final Endpoint endpoint;
	private final EventLoopGroup eventLoopGroup;
	private final boolean checkStatusCode;
	private final HttpHeaders headers;
	private final DefaultClientMonitor monitor;
	
	private final ChannelHandler setHostHeaderHandler;

	public DefaultHttpClient(EventLoopGroup eventLoopGroup, Bootstrap bootstrap, Endpoint endpoint, HttpHeaders headers, SslProvider sslProvider, boolean checkStatusCode,
			DefaultClientMonitor monitor, int poolSize, boolean unpooledChannels, long poolIdleTimeout) {
		if(unpooledChannels) { 
			this.clientFactory=new UnpooledClientChannelFactory(bootstrap, eventLoopGroup, this, sslProvider, monitor);
		} else if(poolSize>0) {
			this.clientFactory=new FixedSizeClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(this,poolIdleTimeout):this, sslProvider,monitor, poolSize);
		} else {
			this.clientFactory=new SimpleClientChannelFactory(bootstrap, eventLoopGroup, poolIdleTimeout>0? new IdleTimeoutHandler(this,poolIdleTimeout):this, sslProvider,monitor);
		}
		this.monitor=monitor;
		this.eventLoopGroup=eventLoopGroup;
		this.endpoint=endpoint;
		this.headers=headers;
		this.checkStatusCode=checkStatusCode;
		this.setHostHeaderHandler = new SetHostHeaderHandler();
	}

	@Override
	public HttpRequestBuilder request() {
		return new DefaultHttpRequestBuilder(eventLoopGroup,clientFactory,endpoint,headers,checkStatusCode);
	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		ch.pipeline().addLast(new HttpClientCodec());
		ch.pipeline().addLast(setHostHeaderHandler);
	}

	@Override
	public ClientMonitor monitor() {
		return monitor;
	}
	
	@Override
	public Future<Void> close() {
		return monitor.idleChannels().close();
	}

}
