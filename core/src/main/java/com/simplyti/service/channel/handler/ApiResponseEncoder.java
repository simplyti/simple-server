package com.simplyti.service.channel.handler;

import java.nio.CharBuffer;
import java.util.List;

import javax.inject.Inject;

import com.jsoniter.output.JsonStream;
import com.simplyti.service.api.ApiResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;

@Sharable
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ApiResponseEncoder extends MessageToMessageEncoder<ApiResponse> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ApiResponse msg, List<Object> out) throws Exception {
		if(msg.response()==null){
			out.add(buildHttpResponse(Unpooled.EMPTY_BUFFER, HttpResponseStatus.NO_CONTENT,msg));
		} else if(msg.response() instanceof CharSequence){
			out.add(buildHttpResponse(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap((CharSequence)msg.response()), CharsetUtil.UTF_8), HttpResponseStatus.OK,msg));
		} else if(msg.response() instanceof ByteBuf){
			out.add(buildHttpResponse((ByteBuf) msg.response(), HttpResponseStatus.OK,msg));
		}else{
			ByteBuf buffer = ctx.alloc().buffer();
			JsonStream.serialize(msg.response(), new ByteBufOutputStream(buffer));
			out.add(buildHttpResponse(buffer, HttpResponseStatus.OK,msg));
		}
	}
	
	private FullHttpResponse buildHttpResponse(ByteBuf content, HttpResponseStatus status, ApiResponse msg) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		if(msg.isKeepAlive()) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		response.content().markReaderIndex();
		return response;
	}

}
