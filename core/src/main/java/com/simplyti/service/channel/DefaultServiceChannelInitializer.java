package com.simplyti.service.channel;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.StartStopMonitor;
import com.simplyti.service.api.filter.HttpRequestFilter;
import com.simplyti.service.api.filter.HttpResponseFilter;
import com.simplyti.service.channel.handler.ApiExceptionHandler;
import com.simplyti.service.channel.handler.ClientChannelHandler;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.ssl.SslHandlerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultServiceChannelInitializer extends ChannelInboundHandlerAdapter implements ServiceChannelInitializer  {

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ClientChannelGroup clientChannelGroup;
	
	private final ServerConfig serverConfig;
	
	private final SslHandlerFactory sslHandlerFactory;
	
	private final StartStopMonitor startStopMonitor;
	
	private final ApiExceptionHandler apiExceptionHandler;
	
	private final Set<HandlerInit> handlers;
	
	private final Set<HttpRequestFilter> requestFilters;
	private final Set<HttpResponseFilter> responseFilters;
	
	private final Optional<EntryChannelInit> entryChannelInit;
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
		initChannel(ctx.channel());
		ctx.pipeline().remove(this);
		ctx.pipeline().fireChannelRegistered();
	}
	
	private void initChannel(Channel channel) {
		log.debug("Connected {}", channel.remoteAddress());
		clientChannelGroup.add(channel);
		channel.closeFuture().addListener(future -> {
			log.debug("Disconnected {}", channel.remoteAddress());
			clientChannelGroup.remove(channel);
		});
		
		ChannelPipeline pipeline = channel.pipeline();
		if(channel instanceof SocketChannel && 
				((InetSocketAddress)channel.localAddress()).getPort()==serverConfig.securedPort()) {
			pipeline.addLast("ssl",sslHandlerFactory.handler(channel));
		}
		
		if(serverConfig.verbose()) {
			pipeline.addLast(new LoggingHandler(LogLevel.INFO));
		}
		
		if(entryChannelInit.isPresent()) {
			entryChannelInit.get().init(pipeline);
		}else {
			pipeline.addLast(new HttpServerCodec());
		}
		
		pipeline.addLast(ClientChannelHandler.NAME, new ClientChannelHandler(startStopMonitor,handlers,requestFilters,responseFilters));
		pipeline.addLast(apiExceptionHandler);
	}

}
