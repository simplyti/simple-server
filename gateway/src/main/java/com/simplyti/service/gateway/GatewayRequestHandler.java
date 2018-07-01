package com.simplyti.service.gateway;

import javax.inject.Inject;

import com.simplyti.service.channel.handler.DefaultBackendRequestHandler;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.request.StreamedHttpRequest;
import com.simplyti.service.exception.NotFoundException;
import com.simplyti.service.exception.ServiceException;
import com.simplyti.service.gateway.balancer.ServiceBalancer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class GatewayRequestHandler extends DefaultBackendRequestHandler {
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	private final ServiceDiscovery serviceDiscovery;
	private final HttpClient client;
	
	private StreamedHttpRequest clientStream;

	@Inject
	public GatewayRequestHandler(HttpClient client,ServiceDiscovery serviceDiscovery) {
		this.client=client;
		this.serviceDiscovery=serviceDiscovery;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
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
		}else { 
			clientStream.send(ReferenceCountUtil.retain(msg));
		}
	}
	
	private void initialSend(ChannelHandlerContext ctx, Endpoint endpoint, HttpRequest request) {
		clientStream = client.withEndpoin(endpoint)
				.withReadTimeout(-1)
				.send(ReferenceCountUtil.retain(request))
				.forEach(resp->ctx.writeAndFlush(ReferenceCountUtil.retain(resp)));
		
		clientStream.channelFuture().addListener(channelFuture->{
			if(!channelFuture.isSuccess()) {
				log.warn("Cannot connect to backend {}: {}",endpoint,channelFuture.cause().toString());
				ctx.fireExceptionCaught(new ServiceException(HttpResponseStatus.BAD_GATEWAY));
			}
		});
	}

}
