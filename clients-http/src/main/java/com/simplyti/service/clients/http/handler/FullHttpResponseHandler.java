package com.simplyti.service.clients.http.handler;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.ClientRequestChannel;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpStatusClass;

public class FullHttpResponseHandler extends HttpObjectAggregator {

	private final ClientRequestChannel<FullHttpResponse> clientChannel;
	private final boolean checkStatusCode;

	public FullHttpResponseHandler(ClientRequestChannel<FullHttpResponse> clientChannel, boolean checkStatusCode) {
		super(52428800);
		this.checkStatusCode = checkStatusCode;
		this.clientChannel = clientChannel;
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientChannel.resultPromise().setFailure(new ClosedChannelException());
    }

	@Override
	protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
		FullHttpResponse response = (FullHttpResponse) aggregated;
		if(!clientChannel.resultPromise().isDone()) {
			if(checkStatusCode && isError(response.status().codeClass())) {
				clientChannel.resultPromise().setFailure(new HttpException(response.status().code()));
			}else {
				clientChannel.resultPromise().setSuccess((FullHttpResponse) aggregated.retain());
			}
		}
		clientChannel.pipeline().remove(this);
		clientChannel.release();
	}
	
	private boolean isError(HttpStatusClass codeClass) {
		return codeClass.equals(HttpStatusClass.CLIENT_ERROR) || 
				codeClass.equals(HttpStatusClass.SERVER_ERROR);
	}

	@Override
    protected void decode(final ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		super.decode(ctx, msg, new ArrayList<>());
	}
	
}
