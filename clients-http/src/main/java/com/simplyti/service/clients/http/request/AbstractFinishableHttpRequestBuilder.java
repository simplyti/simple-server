package com.simplyti.service.clients.http.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.handler.DecodingFullHttpResponseHandler;
import com.simplyti.service.clients.http.handler.FullHttpResponseHandler;
import com.simplyti.service.clients.request.ChannelProvider;
import com.simplyti.service.clients.request.ClientRequestProvider;
import com.simplyti.service.filter.http.HttpRequestFilter;
import com.simplyti.util.concurrent.Future;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Promise;
import lombok.experimental.Delegate;

public abstract class AbstractFinishableHttpRequestBuilder<T> implements BaseFinishableHttpRequestBuilder<T>, HeaderAppendableRequestBuilder<T>, ParamAppendableRequestBuilder<T>, FilterableRequestBuilder<T>, ClientRequestProvider {

	@Delegate(excludes = ParamsAppendBuilder.class)
	protected final HeaderAppendBuilder<T> headerAppend;
	
	@Delegate(excludes = HeaderAppendBuilder.class)
	protected final ParamsAppendBuilder<T> paramsAppend;
	
	protected final ChannelProvider channelProvider;
	protected final HttpMethod method;
	protected final String path;
	
	protected boolean checkStatus;
	protected List<HttpRequestFilter> filters;

	public AbstractFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus) {
		this(channelProvider, method, path, params, headers, checkStatus, null);
	}
	
	@SuppressWarnings("unchecked")
	public AbstractFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, Map<String,Object> params, HttpHeaders headers, boolean checkStatus, List<HttpRequestFilter> filters) {
		this.headerAppend=new HeaderAppendBuilder<T>(headers, (T) this);
		this.paramsAppend=new ParamsAppendBuilder<>(params, (T) this);
		this.channelProvider=channelProvider;
		this.method=method;
		this.path=path;
		this.checkStatus=checkStatus;
		this.filters=filters;
	}
	
	protected AbstractFinishableHttpRequestBuilder(ChannelProvider channelProvider, HttpMethod method, String path, ParamsAppendBuilder<T> paramsAppend, HeaderAppendBuilder<T> headerAppend, boolean checkStatus, List<HttpRequestFilter> filters) {
		this.headerAppend=headerAppend;
		this.paramsAppend=paramsAppend;
		this.channelProvider=channelProvider;
		this.method=method;
		this.path=path;
		this.checkStatus=checkStatus;
		this.filters=filters;
	}
	
	@Override
	public Future<FullHttpResponse> fullResponse() {
		return channelProvider.channel()
				.thenCombine(channel->{
					if(filters!=null) {
						channel.pipeline().fireUserEventTriggered(new HttpRequestFilterEvent(filters));
					}
					ByteBuf buff;
					try{
						buff = body(channel);
					}catch (Throwable e) {
						channel.release();
						return channel.eventLoop().newFailedFuture(e);
					}
					boolean expectedContinue = headerAppend.values() !=null && headerAppend.values().contains(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE, true);
					HttpRequest request = request(expectedContinue, buff);
					Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(new FullHttpResponseHandler(channel,expectedContinue?buff:null,checkStatus,promise));
					channel.writeAndFlush(request).addListener(f->hadleWriteFuture(f,channel,promise));
					return promise;
				});
	}
	
	@Override
	public <U> Future<U> fullResponse(Function<FullHttpResponse, U> fn) {
		return channelProvider.channel()
				.thenCombine(channel->{
					if(filters!=null) {
						channel.pipeline().fireUserEventTriggered(new HttpRequestFilterEvent(filters));
					}
					ByteBuf buff;
					try{
						buff = body(channel);
					}catch (Throwable e) {
						channel.release();
						return channel.eventLoop().newFailedFuture(e);
					}
					boolean expectedContinue = headerAppend.values() !=null && headerAppend.values().contains(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE, true);
					HttpRequest request = request(expectedContinue, buff);
					Promise<U> promise = channel.eventLoop().newPromise();
					channel.pipeline().addLast(new DecodingFullHttpResponseHandler<>(channel,expectedContinue?buff:null,checkStatus,promise, fn));
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
	public ServerSentEventRequestBuilder serverSentEvents() {
		return new DefaultServerSentEventRequestBuilder(channelProvider, null, this, checkStatus, filters);
	}

	@Override
	public StreamedHandledHttpRequestBuilder<ByteBuf> stream() {
		return new DefaultStreamedHandledHttpRequestBuilder(channelProvider, null, this, checkStatus, filters);
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
	public HttpRequest request(boolean expectedContinue, ByteBuf buff) {
		HttpRequest request;
		if(expectedContinue) {
			request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, path);
		} else if(buff!=null) {
			request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path, buff);
		} else {
			request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, path);
		}
		return setHeaders(request,buff).setUri(paramsAppend.withParams(request.uri()));
	}
	
	protected HttpRequest setHeaders(HttpRequest request, ByteBuf buff) {
		this.headerAppend.withHeaders(request);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,buff==null?0:buff.readableBytes());
		return request;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T withFilter(HttpRequestFilter filter) {
		if(filter != null) {
			if(filters==null) {
				filters = new ArrayList<>();
			}
			filters.add(filter);
		}
		return (T) this;
	}

	protected abstract ByteBuf body(ClientChannel ch);

}
