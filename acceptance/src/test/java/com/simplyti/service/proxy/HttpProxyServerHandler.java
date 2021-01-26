package com.simplyti.service.proxy;

import java.net.InetSocketAddress;
import java.util.Base64;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.util.concurrent.Promise;

public class HttpProxyServerHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	 private final Bootstrap b = new Bootstrap();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		if(msg instanceof HttpRequest) {
			if(((InetSocketAddress)ctx.channel().localAddress()).getPort() == 3129) {
				String proxyAuth = (((HttpRequest) msg).headers().get(HttpHeaderNames.PROXY_AUTHORIZATION));
				if(proxyAuth.matches("^Basic\\s.*")) {
					String userPass = new String(Base64.getDecoder().decode(proxyAuth.substring(6)));
					if(!userPass.equals("proxyuser:123456")) {
						DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
	                	response.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
	                	ctx.channel().writeAndFlush(response);
	                	return;
					}
				} else {
					DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                	response.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
                	ctx.channel().writeAndFlush(response);
                	return;
				}
			}
			HttpMethod method = ((HttpRequest) msg).method();
			String uri = ((HttpRequest) msg).uri();
			String[] hostPort = uri.split(":");
//			proxy-authorization: Basic cHJveHl1c2VyOjEyMzQ1Ng==
			if(method.equals(HttpMethod.CONNECT)) {
				ctx.pipeline().remove(this);
				Promise<Channel> promise = ctx.executor().newPromise();
				promise.addListener(future -> {
	            	if (future.isSuccess()) {
	                	final Channel outboundChannel = promise.getNow();
	                	DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
	                	response.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
	                	ChannelFuture responseFuture = ctx.channel().writeAndFlush(response);
	                    responseFuture.addListener(new ChannelFutureListener() {
	                        @Override
	                        public void operationComplete(ChannelFuture channelFuture) {
	                        	ctx.pipeline().remove(HttpServerCodec.class);
	                        	outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
	                            ctx.pipeline().addLast(new RelayHandler(outboundChannel));
	                        }
	                    });
	                } else {
	                    ctx.channel().writeAndFlush( new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
	                    SocksServerUtils.closeOnFlush(ctx.channel());
	                }
		        });
				
				b.group(ctx.channel().eventLoop())
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .handler(new DirectClientHandler(promise));
				
				b.connect(hostPort[0], Integer.parseInt(hostPort[1])).addListener(new ChannelFutureListener() {
	                @Override
	                public void operationComplete(ChannelFuture future) throws Exception {
	                    if (future.isSuccess()) {
	                        // Connection established use handler provided results
	                    } else {
	                        SocksServerUtils.closeOnFlush(ctx.channel());
	                    }
	                }
	            });
			} else {
				ctx.close();
			}
		} else {
			ctx.close();
		}
	}


}
