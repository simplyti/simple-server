package com.simplyti.service.clients.http.handler;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.events.ClientResponseEvent;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.ReferenceCountUtil;

public class FullHttpResponseHandler<T> extends HttpObjectAggregator {

	private final ClientRequestChannel<T> clientChannel;
	private final boolean checkStatusCode;

	public FullHttpResponseHandler(ClientRequestChannel<T> clientChannel, boolean checkStatusCode) {
		super(52428800);
		this.checkStatusCode = checkStatusCode;
		this.clientChannel = clientChannel;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientChannel.setFailure(new ClosedChannelException());
    }

	@Override
	protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
		FullHttpResponse response = (FullHttpResponse) aggregated;
		clientChannel.pipeline().fireUserEventTriggered(new ClientResponseEvent(response));
		if(!clientChannel.isDone()) {
			if(checkStatusCode && isError(response.status().codeClass())) {
				clientChannel.setFailure(new HttpException(response.status().code()));
			}else {
				clientChannel.setSuccess(handle((FullHttpResponse) aggregated));
			}
		}
		clientChannel.pipeline().remove(this);
		clientChannel.release();
	}
	
	@SuppressWarnings("unchecked")
	protected T handle(FullHttpResponse msg) {
		return (T) ReferenceCountUtil.retain(msg);
	}
	
	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}

	@Override
    protected void decode(final ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		super.decode(ctx, msg, new ArrayList<>());
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		clientChannel.setFailure(cause);
    }
	
}
