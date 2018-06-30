package com.simplyti.service.gateway;

import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.Maps;
import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.request.FinishableStreamedHttpRequest;
import com.simplyti.service.exception.NotFoundException;
import com.simplyti.service.exception.ServiceException;
import com.simplyti.service.gateway.balancer.ServiceBalancer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class GatewayRequestHandler implements DefaultBackendRequestHandler{
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final Map<ChannelId,FinishableStreamedHttpRequest> clientsStreams = Maps.newConcurrentMap();

	private final ServiceDiscovery serviceDiscovery;
	private final HttpClient client;
	
	@Inject
	public GatewayRequestHandler(HttpClient client,ServiceDiscovery serviceDiscovery) {
		this.client=client;
		this.serviceDiscovery=serviceDiscovery;
	}
	
	@Override
	public void handle(ChannelHandlerContext ctx, HttpObject msg) {
		if(msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
			
			ServiceBalancer service = serviceDiscovery.get(request.headers().get(HttpHeaderNames.HOST),
					request.method(),decoder.path());
			
			if(service==null) {
				ctx.fireExceptionCaught(new NotFoundException());
			}else {
				Endpoint endpoint = service.next();
				if(endpoint==null) {
					ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.SERVICE_UNAVAILABLE));
				}else {
					initialSend(ctx,endpoint,request);
				}
			}
		}else if(msg instanceof LastHttpContent) {
			clientsStreams.remove(ctx.channel().id()).send(ReferenceCountUtil.retain(msg));
		} else {
			clientsStreams.get(ctx.channel().id()).send(ReferenceCountUtil.retain(msg));
		}
	}
	
	private void initialSend(ChannelHandlerContext ctx, Endpoint endpoint, HttpRequest request) {
		FinishableStreamedHttpRequest stream = client.withEndpoin(endpoint)
				.withReadTimeout(-1)
				.send(ReferenceCountUtil.retain(request))
				.forEach(resp->ctx.writeAndFlush(ReferenceCountUtil.retain(resp)));
			
		clientsStreams.put(ctx.channel().id(), stream);
		
		stream.future().addListener(channelFuture->{
			if(!channelFuture.isSuccess()) {
				log.warn("Cannot connect to backend {}: {}",endpoint,channelFuture.cause().toString());
				ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.BAD_GATEWAY));
				clientsStreams.remove(ctx.channel().id());
			}
		});
	}

}
