package com.simplyti.service.clients.http.handler;

import java.util.ArrayList;
import java.util.List;

import com.simplyti.service.clients.ClientChannel;
import com.simplyti.service.clients.http.exception.HttpException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponse;

public class FullHttpResponseHandler extends HttpObjectAggregator {

	private final ClientChannel<FullHttpResponse> clientChannel;
	private final boolean checkStatusCode;

	public FullHttpResponseHandler(ClientChannel<FullHttpResponse> clientChannel, boolean checkStatusCode) {
		super(10000000);
		this.checkStatusCode = checkStatusCode;
		this.clientChannel = clientChannel;
	}

	@Override
	protected FullHttpMessage beginAggregation(HttpMessage start, ByteBuf content) throws Exception {
		if(checkStatusCode) {
			HttpResponse response = (HttpResponse) start;
			if(response.status().code()>=400) {
				clientChannel.promise().setFailure(new HttpException(response.status().code()));
			}
		}
		return super.beginAggregation(start, content);
	}

	@Override
	protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
		if(!clientChannel.promise().isDone()) {
			clientChannel.promise().setSuccess((FullHttpResponse) aggregated.retain());
		}
		clientChannel.pipeline().remove(this);
		clientChannel.release();
	}
	
	@Override
    protected void decode(final ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		super.decode(ctx, msg, new ArrayList<>());
	}
	
}
