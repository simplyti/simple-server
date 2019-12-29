package com.simplyti.service.clients.http.handler;

import java.nio.channels.ClosedChannelException;

import com.simplyti.service.clients.channel.ClientChannel;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.concurrent.Promise;

public abstract class AbstractFullHttpResponseHandler<T> extends SimpleChannelInboundHandler<FullHttpResponse> {

	private static final String AGGREGATOR = "aggregator";
	
	private final ClientChannel channel;
	private final Promise<T> promise;
	private final String handlerName;
	private final boolean checkStatus;

	public AbstractFullHttpResponseHandler(String handlerName, ClientChannel channel, Promise<T> promise, boolean checkStatus) {
		super(false);
		this.channel=channel;
		this.promise=promise;
		this.handlerName=handlerName;
		this.checkStatus=checkStatus;
	}
	
	@Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ctx.pipeline().addBefore(handlerName, AGGREGATOR, new HttpObjectAggregator(52428800));
    }
	
	@Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		ctx.pipeline().remove(AGGREGATOR);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
		channel.pipeline().remove(this);
		channel.release();
		if(checkStatus && isError(msg.status().codeClass())) {
			msg.release();
			promise.setFailure(new HttpException(msg.status().code()));
		} else {
			promise.setSuccess(handle(msg));
		}
		
	}
	
	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		channel.pipeline().remove(this);
		promise.tryFailure(new ClosedChannelException());
		channel.release();
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		channel.pipeline().remove(this);
		promise.tryFailure(cause);
		channel.close().addListener(f->channel.release());
    }

	protected abstract T handle(FullHttpResponse msg);

}
