package com.simplyti.service.channel;

import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.DefaultService;
import com.simplyti.service.Listener;
import com.simplyti.service.ServerConfig;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.channel.handler.ChannelExceptionHandler;
import com.simplyti.service.channel.handler.ClientChannelHandler;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.ssl.SslHandlerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultServiceChannelInitializer extends ChannelInitializer<Channel> implements ServiceChannelInitializer  {

	private final ClientChannelGroup clientChannelGroup;
	
	private final ServerConfig serverConfig;
	
	private final SslHandlerFactory sslHandlerFactory;
	
	private final StartStopMonitor startStopMonitor;
	
	private final ChannelExceptionHandler channelExceptionHandler;
	
	private final Set<HandlerInit> handlers;
	
	private final Set<HttpRequestFilter> requestFilters;
	private final Set<HttpResponseFilter> responseFilters;
	
	private final EntryChannelInit entryChannelInit;
	
	@Override
	protected void initChannel(Channel channel) throws Exception {
		clientChannelGroup.add(channel);
		ChannelPipeline pipeline = channel.pipeline();
		
		if(isSslChannel(channel)) {
			pipeline.addLast("ssl",sslHandlerFactory.handler(channel));
		}
		
		if(serverConfig.verbose()) {
			pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		}
		
		entryChannelInit.init(pipeline);
		pipeline.addLast(ClientChannelHandler.NAME, new ClientChannelHandler(startStopMonitor,handlers,requestFilters,responseFilters));
		pipeline.addLast(channelExceptionHandler);
	}

	private boolean isSslChannel(Channel channel) {
		Listener listener = channel.parent() != null ? channel.parent().attr(DefaultService.LISTENER_ATT).get() : null;
		return listener !=null? listener.ssl() : false;
	}

}
