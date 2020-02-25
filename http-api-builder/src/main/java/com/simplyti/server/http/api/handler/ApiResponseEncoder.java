package com.simplyti.server.http.api.handler;

import java.nio.CharBuffer;
import java.util.List;

import javax.inject.Inject;

import com.simplyti.service.api.ApiResponse;
import com.simplyti.service.api.serializer.json.Json;

import io.netty.buffer.ByteBuf;
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
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;

@Sharable
@AllArgsConstructor(onConstructor=@__(@Inject))
public class ApiResponseEncoder extends MessageToMessageEncoder<ApiResponse> {
	
	private final Json json;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ApiResponse msg, List<Object> out) throws Exception {
		if(msg.response() == null && msg.notFoundOnNull()) {
			out.add(buildHttpResponse(Unpooled.EMPTY_BUFFER, HttpResponseStatus.NOT_FOUND,msg,null));
		} else if(msg.response()==null){
			out.add(buildHttpResponse(Unpooled.EMPTY_BUFFER, HttpResponseStatus.NO_CONTENT,msg,null));
		} else if(msg.response() instanceof CharSequence){
			out.add(buildHttpResponse(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap((CharSequence)msg.response()), CharsetUtil.UTF_8), HttpResponseStatus.OK,msg,
					HttpHeaderValues.TEXT_PLAIN));
		} else if(msg.response() instanceof ByteBuf){
			out.add(buildHttpResponse((ByteBuf) msg.response(), HttpResponseStatus.OK,msg,null));
		} else{
			ByteBuf buffer = ctx.alloc().buffer();
			try {
				json.serialize(msg.response(), buffer);
				out.add(buildHttpResponse(buffer, HttpResponseStatus.OK,msg,HttpHeaderValues.APPLICATION_JSON));
			} catch (Throwable error) {
				buffer.release();
				throw error;
			}
		}
	}
	
	private FullHttpResponse buildHttpResponse(ByteBuf content, HttpResponseStatus status, ApiResponse msg, AsciiString contentType) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content, false);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
		if(contentType!=null) {
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
		}
		if(msg.isKeepAlive()) {
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		response.content().markReaderIndex();
		return response;
	}

}
