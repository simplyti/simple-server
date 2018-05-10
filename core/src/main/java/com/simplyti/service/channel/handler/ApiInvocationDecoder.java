package com.simplyti.service.channel.handler;

import java.util.List;


import com.simplyti.service.api.ApiInvocation;
import com.simplyti.service.api.ApiMacher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;

public class ApiInvocationDecoder extends MessageToMessageDecoder<FullHttpRequest>{
	
	private final ApiMacher apiMacher;
	
	public ApiInvocationDecoder(ApiMacher apiMacher) {
		this.apiMacher=apiMacher;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
		out.add(new ApiInvocation(apiMacher.operation(),apiMacher.matcher(),apiMacher.parameters(),msg));
	}

}
