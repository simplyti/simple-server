package com.simplyti.service.aws.lambda.coders;

import java.util.List;

import javax.inject.Inject;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor=@__(@Inject))
public class AwsHttpLambdaRequestDecoder extends MessageToMessageDecoder<ByteBuf>{
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		byte[] data = new byte[msg.readableBytes()];
		msg.readBytes(data);
		Any event = JsonIterator.deserialize(data);
		out.add(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, 
				HttpMethod.valueOf(event.get("httpMethod").toString()), 
				event.get("path").toString(),
				Unpooled.EMPTY_BUFFER));
	}

}
