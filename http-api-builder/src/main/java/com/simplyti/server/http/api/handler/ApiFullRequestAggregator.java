package com.simplyti.server.http.api.handler;

import java.util.List;

import com.simplyti.server.http.api.request.ApiMatchRequest;
import com.simplyti.service.config.ServerConfig;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.ReferenceCountUtil;

public class ApiFullRequestAggregator extends HttpObjectAggregator {

	private boolean apiMatch;
	
	public ApiFullRequestAggregator(ServerConfig config) {
		super(config.maxBodySize());
	}
	
	protected void decode(final ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		if(apiMatch) {
			super.decode(ctx, msg, out);
		} else {
			ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
		}
	}
	
	protected void finishAggregation(FullHttpMessage aggregated) throws Exception {
		apiMatch = false;
	}
	
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof ApiMatchRequest) {
			this.apiMatch = true;
		}
		ctx.fireUserEventTriggered(evt);
    }

}
