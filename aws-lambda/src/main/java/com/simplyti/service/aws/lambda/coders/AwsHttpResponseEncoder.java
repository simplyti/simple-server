package com.simplyti.service.aws.lambda.coders;

import com.google.common.collect.ImmutableMap;
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
		ImmutableMap<String, Object> awsResponse = ImmutableMap.<String,Object>builder()
				.put("statusCode",msg.status().code())
				.put("body",msg.content().toString(CharsetUtil.UTF_8))
				.build();
		JsonStream.serialize(awsResponse, new ByteBufOutputStream(out));
	}

}
