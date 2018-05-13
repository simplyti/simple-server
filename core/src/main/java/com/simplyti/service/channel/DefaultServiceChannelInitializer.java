package com.simplyti.service.channel;

import java.net.InetSocketAddress;
import java.util.Optional;

import javax.inject.Inject;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.Service;
import com.simplyti.service.channel.handler.ApiExceptionHandler;
import com.simplyti.service.channel.handler.ClientChannelHandler;
import com.simplyti.service.channel.handler.inits.ApiRequestHandlerInit;
import com.simplyti.service.channel.handler.inits.DefaultBackendHandlerInit;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultServiceChannelInitializer extends ChannelInboundHandlerAdapter implements ServiceChannelInitializer  {

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ClientChannelGroup clientChannelGroup;
	
	private final ServerConfig serverConfig;
	
	private final SslContext sslCtx;
	
	private final Service<?> service;
	
	private final ApiExceptionHandler apiExceptionHandler;
	
	private final ApiRequestHandlerInit apiRequestHandlerInit;
	private final FileServerHandlerInit fileServerHandlerInit;
	private final DefaultBackendHandlerInit defaultBackendFullRequestHandlerInit;
	
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
		if(channel instanceof SocketChannel && ((InetSocketAddress)channel.localAddress()).getPort()==serverConfig.securedPort()) {
			pipeline.addLast(sslCtx.newHandler(channel.alloc()));
		}
		
		if(entryChannelInit.isPresent()) {
			entryChannelInit.get().init(pipeline);
		}else {
			pipeline.addLast(new HttpServerCodec());
		}
		
		pipeline.addLast(ClientChannelHandler.NAME, new ClientChannelHandler(service,apiRequestHandlerInit,fileServerHandlerInit,defaultBackendFullRequestHandlerInit));
		pipeline.addLast(apiExceptionHandler);
	}

}
