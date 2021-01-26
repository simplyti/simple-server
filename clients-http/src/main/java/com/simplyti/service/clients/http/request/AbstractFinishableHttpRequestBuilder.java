package com.simplyti.service.clients.http.request;

import java.util.Map;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.DecodingFullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.stream.ClientRequestProvider;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Promise;
import lombok.experimental.Delegate;

public abstract class AbstractFinishableHttpRequestBuilder<T> implements BaseFinishableHttpRequestBuilder<T>, HeaderAppendableRequestBuilder<T>, ParamAppendableRequestBuilder<T>, FilterableRequestBuilder<T>, ClientRequestProvider {

	@Delegate(excludes = ParamsAppendBuilder.class)
	private final HeaderAppendBuilder<T> headerAppend;
	
	@Delegate(excludes = HeaderAppendBuilder.class)
	private final ParamsAppendBuilder<T> paramsAppend;
	
	protected final ChannelProvider channelProvider;
	protected final HttpMethod method;
	protected final String path;
	protected boolean checkStatus;

	@SuppressWarnings("unchecked")
	public AbstractFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		this.headerAppend=new HeaderAppendBuilder<T>(headers, (T) this);
		this.paramsAppend=new ParamsAppendBuilder<>(params, (T) this);
		this.channelProvider=channelProvider;
		this.method=method;
		this.path=path;
		this.checkStatus=checkStatus;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return channelProvider.channel()
				.thenCombine(channel->{
					HttpRequest request;
					try{
						request = request(channel);
					}catch (Throwable e) {
						channel.release();
						return channel.eventLoop().newFailedFuture(e);
					}
					Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(new FullHttpResponseHandler(channel,checkStatus,promise));
					channel.writeAndFlush(request).addListener(f->hadleWriteFuture(f,channel,promise));
					return promise;
				});
	}
	
	@Override
	public <U> Future<U> fullResponse(Function<FullHttpResponse, U> fn) {
		return channelProvider.channel()
				.thenCombine(channel->{
					HttpRequest request;
					try{
						request = request(channel);
					}catch (Throwable e) {
						channel.release();
						return channel.eventLoop().newFailedFuture(e);
					}
					Promise<U> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(new DecodingFullHttpResponseHandler<>(channel,checkStatus,promise, fn));
					channel.writeAndFlush(request).addListener(f->hadleWriteFuture(f,channel,promise));
					return promise;
				});
	}
	
	private void hadleWriteFuture(io.netty.util.concurrent.Future<? super Void> future, ClientChannel channel, Promise<?> promise) {
		if(future.isSuccess()) {
			connectSuccess(channel);
		} else {
			promise.tryFailure(future.cause());
			connectFailure(channel,future.cause());
		}
	}

	protected void connectFailure(ClientChannel channel, Throwable cause) {
		channel.close().addListener(f->channel.release());
	}

	protected void connectSuccess(ClientChannel channel) {
		// No-op
	}

	@Override
	public StreamedHandledHttpRequestBuilder<ByteBuf> stream() {
		return new DefaultStreamedHandledHttpRequestBuilder(channelProvider, null, this, checkStatus);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withCheckStatusCode() {
		this.checkStatus=true;
		return (T) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withIgnoreStatusCode() {
		this.checkStatus=false;
		return (T) this;
	}
	
	@Override
	public HttpRequest request(ClientChannel ch) {
		FullHttpRequest request = buildRequest(ch);
		return setHeaders(request,ch).setUri(paramsAppend.withParams(request.uri()));
	}

	protected FullHttpRequest buildRequest(ClientChannel ch) {
		ByteBuf buff = body(ch);
		if(buff!=null) {
			return new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path, buff);
		} else {
			return new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path);
		}
	}

	private FullHttpRequest setHeaders(FullHttpRequest request,ClientChannel ch) {
		this.headerAppend.withHeaders(request);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
		return request;
	}

	protected abstract ByteBuf body(ClientChannel ch);

}
