package com.simplyti.service.clients.http.handler;

import java.util.List;

import com.simplyti.service.filter.FilterChain;
import com.simplyti.service.filter.http.HttpRequestFilter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public class HttpRequestFilterHandler extends ChannelOutboundHandlerAdapter {


	private final List<HttpRequestFilter> filters;
	
	private boolean flushed;
	private boolean done;

	public HttpRequestFilterHandler(List<HttpRequestFilter> filters) {
		this.filters=filters;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		FilterChain<HttpRequest> chain = FilterChain.of(filters,ctx,(FullHttpRequest)msg);
		chain.execute()
			.thenAccept(handled->{
				ctx.pipeline().remove(this);
				this.done=true;
				if(!handled) {
					ctx.write(msg, promise);
					if(flushed) {
						ctx.flush();
					}
				} else {
					promise.setSuccess(null);
				}
			})
			.onError(cause->{
				ctx.pipeline().remove(this);
				promise.setFailure(cause);
			});
	}
	
    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        this.flushed=true;
        if(this.done) {
        	ctx.flush();
        }
    }

}
