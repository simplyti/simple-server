package com.simplyti.service.api;

import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
	
	private final ChannelHandlerContext ctx;
	private final boolean isKeepAlive;
	private final ExceptionHandler exceptionHandler;
	private final ApiMacher matcher;
	private final ApiInvocation invocation;
	private final SyncTaskSubmitter syncTaskSubmitter;
	
	private final Map<String,String> cachedpathParams = new HashMap<>();
	
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
			return ctx.writeAndFlush(new ApiResponse(response,isKeepAlive))
					.addListener(this::writeListener);
		}else {
			ReferenceCountUtil.release(response);
			tryRelease();
			log.warn("Cannot write response, channel {} already closed",ctx.channel().remoteAddress());
			return ctx.channel().eventLoop().newFailedFuture(new ClosedChannelException());
		}
	}
	
	@SuppressWarnings("unchecked")
	public Future<Void> send(HttpObject response) {
		if(response==null) {
			return send((O) response);
		}
		return ctx.writeAndFlush(response)
			.addListener(this::writeListener);
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
		return cachedpathParams.computeIfAbsent(key, theKey->{
			Integer group = invocation.operation().pathParamNameToGroup().get(key);
			if(group==null){
				return null;
			}else{
				return matcher.matcher().group(group);
			}
		});
	}
	
	@Override
	public String queryParam(String name) {
		if(this.matcher.parameters().containsKey(name)) {
			List<String> params = this.matcher.parameters().get(name);
			if(params.isEmpty()) {
				return null;
			}else {
				return params.get(0);
			}
		}else {
			return null;
		}
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
		ReferenceCountUtil.release(this);
		return ctx.close();
	}
	
	@Override
	public Future<O> sync(Callable<O> task) {
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
