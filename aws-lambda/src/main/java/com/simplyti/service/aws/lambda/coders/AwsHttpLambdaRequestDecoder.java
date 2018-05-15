package com.simplyti.service.aws.lambda.coders;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.spi.TypeLiteral;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
				path(event),
				ByteBufUtil.writeUtf8(ctx.alloc(), event.get("body").toString())));
	}

	private String path(Any event) {
		String path = event.get("path").toString();
		Any queryStringObject = event.get("queryStringParameters");
		if(queryStringObject.valueType()==ValueType.OBJECT) {
			Map<String,String> parameters = queryStringObject.as(new TypeLiteral<Map<String,String>>(){});
			if(parameters.isEmpty()) {
				return path;
			}else {
				return Joiner.on('?').join(path, Joiner.on('&').withKeyValueSeparator('=').join(parameters));
			}
		}else {
			return path;
		}
	}

}