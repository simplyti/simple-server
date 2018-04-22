package com.simplyti.service.client;

import javax.security.cert.X509Certificate;

import com.google.common.base.MoreObjects;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class DefaultSimpleHttpClient implements SimpleHttpClient {

	private final ClientChannelPool channelPoolMap;
	private final EventLoopGroup eventLoopGroup;
	
	private final ServerCertificateHandler serverCertificateHandler;

	public DefaultSimpleHttpClient(EventLoopGroup eventLoopGroup) {
		this.eventLoopGroup=eventLoopGroup;
		serverCertificateHandler = new  ServerCertificateHandler();
		this.channelPoolMap = new ClientChannelPool(eventLoopGroup,serverCertificateHandler);
	}

	public Future<SimpleHttpResponse> send(Object... objs) {
		return sendPort("localhost",8080,false,objs);
	}
	
	public Future<SimpleHttpResponse> sendPort(String host, int port, boolean ssl, Object... objs) {
		return sendPort(host,host,port,ssl,objs);
	}
	
	public Future<SimpleHttpResponse> sendPort(String host,String sni, int port, boolean ssl, Object... objs) {
		Promise<SimpleHttpResponse> promise = eventLoopGroup.next().newPromise();
		ChannelPool pool = channelPoolMap.get(new ServerAddress(host,MoreObjects.firstNonNull(sni, host), port,ssl));
		Future<Channel> channelFuture = pool.acquire();
		channelFuture.addListener(f->{
			if(channelFuture.isSuccess()) {
				Channel channel = channelFuture.getNow();
				channel.pipeline().addLast(new ClientResponseHandler(promise,pool));
				for(Object obj:objs) {
					channel.write(obj);
				}
				channel.flush();
			}else {
				promise.setFailure(f.cause());
			}
		});
		return promise;
	}

	@Override
	public Future<SimpleHttpResponse> get(HttpVersion version, String path,int port, boolean ssl,String sni, HttpHeaders headers) {
		DefaultFullHttpRequest req = new DefaultFullHttpRequest(MoreObjects.firstNonNull(version, HttpVersion.HTTP_1_1), HttpMethod.GET, path);
		req.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
		if(headers!=null) {
			req.headers().add(headers);
		}
		return sendPort("localhost",sni,port,ssl,req);
	}

	@Override
	public Future<SimpleHttpResponse> post(String path, String body) {
		ByteBuf content;
		if(body!=null) {
			 content = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
		}else {
			content = Unpooled.EMPTY_BUFFER;
		}
		DefaultFullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path,content);
		req.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
		return send(req);
	}
	
	@Override
	public Future<SimpleHttpResponse> delete(String path) {
		DefaultFullHttpRequest req = new DefaultFullHttpRequest(MoreObjects.firstNonNull(HttpVersion.HTTP_1_1, HttpVersion.HTTP_1_1), HttpMethod.DELETE, path);
		req.headers().set(HttpHeaderNames.CONTENT_LENGTH,0);
		return send(req);
	}

	@Override
	public int activeConnections() {
		return channelPoolMap.activeConnections();
	}

	@Override
	public X509Certificate lastServerCertificate() {
		return serverCertificateHandler.lastCertificate();
	}

}
