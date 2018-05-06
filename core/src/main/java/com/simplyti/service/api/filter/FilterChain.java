package com.simplyti.service.api.filter;

import java.util.Iterator;
import java.util.Set;

import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.ApiOperation;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class FilterChain implements FilterContext {

	private final Promise<Void> promise;
	private final Iterator<OperationInboundFilter> iterator;
	private final ApiInvocation<?> msg;

	private FilterChain(Set<OperationInboundFilter> filters, ChannelHandlerContext ctx, ApiInvocation<?> msg) {
		this.iterator =  filters.iterator();
		this.promise = ctx.executor().newPromise();
		this.msg=msg;
	}

	public static FilterChain of(Set<OperationInboundFilter> filters, ChannelHandlerContext ctx, ApiInvocation<?> msg) {
		return new FilterChain(filters,ctx,msg);
	}

	public Future<Void> execute() {
		iterator.next().execute(this);
		return promise;
	}

	@Override
	public void done() {
		if(iterator.hasNext()) {
			iterator.next().execute(this);
		}else {
			promise.setSuccess(null);
		}
	}

	@Override
	public HttpHeaders headers() {
		return msg.headers();
	}

	@Override
	public void fail(Throwable cause) {
		promise.setFailure(cause);
	}

	@Override
	public ApiOperation<?, ?> operation() {
		return msg.operation();
	}

}
