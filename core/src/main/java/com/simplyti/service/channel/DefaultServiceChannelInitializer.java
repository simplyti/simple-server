package com.simplyti.service.channel;


import java.net.InetSocketAddress;

import javax.inject.Inject;
import javax.inject.Provider;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.channel.handler.ApiExceptionHandler;
import com.simplyti.service.channel.handler.ApiInvocationDecoder;
import com.simplyti.service.channel.handler.ApiInvocationHandler;
import com.simplyti.service.channel.handler.ApiResponseEncoder;
import com.simplyti.service.channel.handler.ClientChannelActiveHandler;
import com.simplyti.service.channel.handler.FileServeHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultServiceChannelInitializer extends ChannelInboundHandlerAdapter implements ServiceChannelInitializer  {

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final Provider<FileServeHandler> fileServeHandler;
	
	private final ApiInvocationDecoder apiInvocationDecoder;
	private final ApiResponseEncoder apiResponseEncoder;
	private final ApiInvocationHandler apiInvocationHandler;
	private final ApiExceptionHandler apiExceptionHandler;
	
	private final ClientChannelActiveHandler clientChannelActiveHandler;
	
	private final ClientChannelGroup clientChannelGroup;
	
	private final ServerConfig serverConfig;
	
	private final SslContext sslCtx;
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
		initChannel(SocketChannel.class.cast(ctx.channel()), ctx);
		ctx.pipeline().remove(this);
		ctx.pipeline().fireChannelRegistered();
	}
	
	private void initChannel(SocketChannel ch, ChannelHandlerContext ctx) {
		log.debug("Connected {}", ch.remoteAddress());
		clientChannelGroup.add(ch);
		ch.closeFuture().addListener(future -> {
			log.debug("Disconnected {}", ch.remoteAddress());
			clientChannelGroup.remove(ch);
		});
		
		ChannelPipeline pipeline = ctx.pipeline();
		if(((InetSocketAddress)ctx.channel().localAddress()).getPort()==serverConfig.securedPort()) {
			pipeline.addLast(sslCtx.newHandler(ctx.alloc()));
		}
		
		pipeline.addLast(new HttpResponseEncoder());
		pipeline.addLast(new HttpRequestDecoder());
		pipeline.addLast(new HttpObjectAggregator(10000000));
		
		if(serverConfig.fileServe()!=null) {
			pipeline.addLast(new ChunkedWriteHandler());
			pipeline.addLast(fileServeHandler.get());
		}
		
		pipeline.addLast(clientChannelActiveHandler);
		
		pipeline.addLast(apiResponseEncoder);
		pipeline.addLast(apiInvocationDecoder);
		
		pipeline.addLast(apiInvocationHandler);
		pipeline.addLast(apiExceptionHandler);
	}

}
