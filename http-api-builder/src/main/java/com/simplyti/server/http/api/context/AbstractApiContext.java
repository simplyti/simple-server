package com.simplyti.server.http.api.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import com.simplyti.server.http.api.builder.ws.WebSocketApiContextConsumer;
import com.simplyti.server.http.api.context.ws.WebSocketApiContextImpl;
import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.sync.SyncTaskSubmitter;
import com.simplyti.util.concurrent.DefaultFuture;
import com.simplyti.util.concurrent.Future;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

public abstract class AbstractApiContext implements ApiContext {
	
	private static final String STRING = "string";
	private static final String INTEGER = "integer";
	private static final String LONG = "long";
	private static final String BOOLEAN = "boolean";
	
	private final SyncTaskSubmitter syncTaskSubmitter;
	private final ChannelHandlerContext ctx;
	private final HttpRequest request;
	private final ApiMatchRequest matcher;
	
	private final Map<String,Object> convertedPathParams = new HashMap<>();
	private final Map<String,Object> convertedQueryParams = new HashMap<>();


	public AbstractApiContext(SyncTaskSubmitter syncTaskSubmitter, ChannelHandlerContext ctx, HttpRequest request, ApiMatchRequest matcher) {
		this.syncTaskSubmitter=syncTaskSubmitter;
		this.ctx=ctx;
		this.request=request;
		this.matcher=matcher;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T queryParam(String name, String type, Function<String,T> fn, T defaultValue) {
		return (T) this.convertedQueryParams.computeIfAbsent(String.format("%s.%s", type,name), key->{
			if(this.matcher.parameters().containsKey(name)) {
				List<String> params = this.matcher.parameters().get(name);
				if(params.isEmpty()) {
					return defaultValue;
				}else {
					return fn.apply(params.get(0));
				}
			}else {
				return defaultValue;
			}
		});
	}
	
	@Override
	public String queryParam(String name) {
		return queryParam(name,STRING,Function.identity(),null);
	}
	
	@Override
	public boolean queryParaAsBoolean(String name) {
		return queryParam(name,BOOLEAN,this::parseBoolean,false);
	}
	
	private boolean parseBoolean(String value) {
		if(value.isEmpty()) {
			return true;
		} else {
			return Boolean.parseBoolean(value);
		}
	}
	
	@Override
	public Integer queryParamAsInt(String name) {
		return queryParam(name,INTEGER,Integer::parseInt,null);
	}
	
	public Integer queryParamAsInt(String name, int defaultValue) {
		return queryParam(name,INTEGER,Integer::parseInt,defaultValue);
	}
	
	@Override
	public Long queryParamAsLong(String name) {
		return queryParam(name,LONG,Long::parseLong,null);
	}
	
	public Long queryParamAsLong(String name, long defaultValue) {
		return queryParam(name,LONG,Long::parseLong,defaultValue);
	}

	
	@SuppressWarnings("unchecked")
	private <T> T pathParam(String key, String type, Function<String,T> fn) {
		return (T) convertedPathParams.computeIfAbsent(String.format("%s.%s", type,key), theKey->{
			Integer group = matcher.operation().pathParamNameToGroup().get(key);
			if(group==null){
				return null;
			}else{
				return fn.apply(matcher.group(group));
			}
		});
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
	public Channel channel() {
		return ctx.channel();
	}
	
	@Override
	public EventExecutor executor() {
		return ctx.executor();
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
	public HttpRequest request() {
		return request;
	}
	
	@Override
	public <T> Future<T> sync(Callable<T> task) {
		return syncTaskSubmitter.submit(executor(), task);
	}
	
	@Override
	public Future<Void> webSocket(WebSocketApiContextConsumer consumer) {
		String location = this.request.headers().get(HttpHeaderNames.HOST) + this.request.uri();
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(location, null, true);
		WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(this.request);
		if (handshaker == null) {
			return new DefaultFuture<>(this.ctx.executor().newFailedFuture(new RuntimeException()),this.ctx.executor());
        } else {
            ChannelFuture channelFuture = handshaker.handshake(this.ctx.channel(), this.request);
            if(channelFuture.isDone()) {
            	if(channelFuture.isSuccess()) {
            		WebSocketApiContextImpl wsCtx = new WebSocketApiContextImpl(ctx);
            		this.ctx.pipeline().addLast(wsCtx);
            		consumer.accept(wsCtx);
            		return new DefaultFuture<>(this.ctx.executor().newSucceededFuture(null),this.ctx.executor());
            	} else {
            		return new DefaultFuture<>(this.ctx.executor().newFailedFuture(new RuntimeException()),this.ctx.executor());
            	}
            } else {
            	Promise<Void> promise = this.ctx.executor().newPromise();
            	channelFuture.addListener(f->{
            		if(f.isSuccess()) {
            			WebSocketApiContextImpl wsCtx = new WebSocketApiContextImpl(ctx);
                		this.ctx.pipeline().addLast(wsCtx);
                		consumer.accept(wsCtx);
            			promise.setSuccess(null);
            		} else {
            			promise.setFailure(new RuntimeException());
            		}
            	});
            	return new DefaultFuture<>(promise,this.ctx.executor());
            }
        }
	}

}
