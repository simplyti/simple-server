package com.simplyti.service.clients.ssl;

import java.util.Collections;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import com.simplyti.service.clients.Endpoint;

import io.netty.channel.Channel;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;

public class SSLChannelInitializer extends AbstractChannelPoolHandler{
	
	private final SslContext sslCtx;
	private final ChannelPoolHandler nestedInitializer;
	private final Endpoint endpoint;
	private final String sniHostName;

	@SneakyThrows
	public SSLChannelInitializer(SslProvider sslProvider, ChannelPoolHandler nestedInitializer, Endpoint endpoint) {
		SslContextBuilder builder = SslContextBuilder
				.forClient();
		if(endpoint instanceof SSLEndpoint) {
			SSLEndpoint sslEndpoint = (SSLEndpoint) endpoint;
			if(sslEndpoint.keyManager() != null) {
				builder.keyManager(sslEndpoint.keyManager());
			}
			this.sniHostName=sslEndpoint.sniHostName();
		} else {
			this.sniHostName=null;
		}
		this.sslCtx = builder
				.sslProvider(sslProvider)
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
		this.nestedInitializer=nestedInitializer;
		this.endpoint=endpoint;
	}
	
	@Override
	public void channelCreated(Channel ch) throws Exception {
	    SSLEngine engine = sslCtx.newEngine(ch.alloc(),endpoint.address().host(),endpoint.address().port());
	    
	    if(sniHostName!=null) {
	    	SSLParameters sslParameters = new SSLParameters();
		    sslParameters.setServerNames(Collections.singletonList(new SNIHostName(sniHostName)));
		    engine.setSSLParameters(sslParameters);
	    }
	    
		SslHandler context = new SslHandler(engine);
		ch.pipeline().addLast(context);
		nestedInitializer.channelCreated(ch);
	}
	
}
