package com.simplyti.service.api;

import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import com.simplyti.service.exception.ExceptionHandler;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.service.sync.VoidCallable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class AbstractApiInvocationContext<O> implements APIContext<O> {
	
	private static final InternalLogger log = InternalLoggerFactory.getInstance(AbstractApiInvocationContext.class);

	private static final String STRING = "string";
	private static final String INTEGER = "integer";
	private static final String LONG = "long";
	private static final String FLOAT = "float";
	private static final String DOUBLE = "double";

	
	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;
	private final ExceptionHandler exceptionHandler;
	private final ApiMacher matcher;
	private final ApiInvocation invocation;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private final Map<String,Object> convertedPathParams = new HashMap<>();
	private final Map<String,Object> convertedQueryParams = new HashMap<>();
	
	private boolean released = false ;

	public AbstractApiInvocationContext(ChannelHandlerContext ctx,ApiMacher matcher, ApiInvocation invocation, ExceptionHandler exceptionHandler,
			SyncTaskSubmitter syncTaskSubmitter) {
		this.ctx=ctx;
		this.matcher=matcher;
		this.invocation=invocation;
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.exceptionHandler=exceptionHandler;
		this.isKeepAlive=HttpUtil.isKeepAlive(invocation.request());
	}

	@Override
	public Future<Void> send(O response) {
		tryRelease();
		if(ctx.channel().isActive()) {
			return ctx.writeAndFlush(new ApiResponse(response,isKeepAlive,matcher.operation().notFoundOnNull()))
					.addListener(this::writeListener);
		}else {
			return closed(response);
		}
	}
	
	private Future<Void> closed(Object response) {
		ReferenceCountUtil.release(response);
		tryRelease();
		log.warn("Cannot write response, channel {} already closed",ctx.channel().remoteAddress());
		return ctx.channel().eventLoop().newFailedFuture(new ClosedChannelException());
	}

	@SuppressWarnings("unchecked")
	public Future<Void> send(HttpObject response) {
		if(response==null) {
			return send((O) response);
		}else if(ctx.channel().isActive()) {
			return ctx.writeAndFlush(response)
					.addListener(this::writeListener);
		}else {
			return closed(response);
		}
	}
	
	@Override
	public Future<Void> failure(Throwable error) {
		tryRelease();
		return exceptionHandler.exceptionCaught(ctx,error);
	}
	
	@Override
	public EventExecutor executor() {
		return ctx.executor();
	}
	
	@Override
	public HttpRequest request() {
		return invocation.request();
	}
	
	@Override
	public String pathParam(String key) {
		return pathParam(key,STRING,Function.identity());
	}
	
	@Override
	public Integer pathParamAsInt(String key) {
		return pathParam(key,INTEGER,Integer::parseInt);
	}
	
	@Override
	public Long pathParamAsLong(String key) {
		return pathParam(key,LONG,Long::parseLong);
	}
	
	@Override
	public Float pathParamAsFloat(String key) {
		return pathParam(key,FLOAT,Float::parseFloat);
	}
	
	@Override
	public Double pathParamAsDouble(String key) {
		return pathParam(key,DOUBLE,Double::parseDouble);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T pathParam(String key, String type, Function<String,T> fn) {
		return (T) convertedPathParams.computeIfAbsent(String.format("%s.%s", type,key), theKey->{
			Integer group = invocation.operation().pathParamNameToGroup().get(key);
			if(group==null){
				return null;
			}else{
				return fn.apply(matcher.matcher().group(group));
			}
		});
	}

	@Override
	public String queryParam(String name) {
		return queryParam(name,STRING,Function.identity());
	}
	
	@Override
	public Integer queryParamAsInt(String name) {
		return queryParam(name,INTEGER,Integer::parseInt);
	}
	
	@Override
	public Long queryParamAsLong(String name) {
		return queryParam(name,LONG,Long::parseLong);
	}
	
	@Override
	public Float queryParamAsFloat(String name) {
		return queryParam(name,FLOAT,Float::parseFloat);
	}
	
	@Override
	public Double queryParamAsDouble(String name) {
		return queryParam(name,DOUBLE,Double::parseDouble);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T queryParam(String name, String type, Function<String,T> fn) {
		return (T) this.convertedQueryParams.computeIfAbsent(String.format("%s.%s", type,name), key->{
			if(this.matcher.parameters().containsKey(name)) {
				List<String> params = this.matcher.parameters().get(name);
				if(params.isEmpty()) {
					return null;
				}else {
					return fn.apply(params.get(0));
				}
			}else {
				return null;
			}
		});
		
	}

	@Override
	public List<String> queryParams(String name) {
		return this.matcher.parameters().get(name);
	}
	
	@Override
	public Map<String,List<String>> queryParams() {
		return this.matcher.parameters();
	}
	
	@Override
	public Channel channel() {
		return ctx.channel();
	}
	
	@Override
	public Future<Void> close() {
		tryRelease();
		return ctx.close();
	}
	
	@Override
	public <U> Future<U> sync(Callable<U> task) {
		return syncTaskSubmitter.submit(ctx.executor(), task);
	}
	
	@Override
	public Future<Void> sync(VoidCallable task) {
		return syncTaskSubmitter.submit(ctx.executor(), task);
	}
	
	public void writeListener(Future<? super Void> f) {
		if(f.isSuccess()) {
			if(!isKeepAlive) {
				ctx.channel().close();
			}
			tryRelease();
		}else {
			failure(f.cause());
		}
	}
	
	public void tryRelease() {
		if(!released) {
			release0();
		}
	}
	
	private void release0() {
		ReferenceCountUtil.release(this);
		released=true;
	}
	
}
