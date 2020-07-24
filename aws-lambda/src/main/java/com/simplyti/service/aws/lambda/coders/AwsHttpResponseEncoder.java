package com.simplyti.service.aws.lambda.coders;

import java.util.HashMap;
import java.util.Map;

import com.jsoniter.output.JsonStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

@Sharable
public class AwsHttpResponseEncoder extends MessageToByteEncoder<FullHttpResponse> {

	@Override
	protected void encode(ChannelHandlerContext ctx, FullHttpResponse msg, ByteBuf out) throws Exception {
		Map<String,Object> awsResponse = new HashMap<>();
		awsResponse.put("statusCode",msg.status().code());
		awsResponse.put("body",msg.content().toString(CharsetUtil.UTF_8));
		
		Map<String,Object> headers = new HashMap<>();
		msg.headers().forEach(header->headers.put(header.getKey(), header.getValue()));
		awsResponse.put("headers",headers);
		
		JsonStream.serialize(awsResponse, new ByteBufOutputStream(out));
	}

}
