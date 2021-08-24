package com.simplyti.server.http.api.handler;

import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.List;

import javax.inject.Inject;

import com.simplyti.server.http.api.handler.message.ApiBufferResponse;
import com.simplyti.server.http.api.handler.message.ApiObjectResponse;
import com.simplyti.server.http.api.handler.message.ApiResponse;
import com.simplyti.server.http.api.handler.message.ApiCharSequenceResponse;
import com.simplyti.service.api.serializer.json.Json;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;

@Sharable
@AllArgsConstructor(onConstructor=@__(@Inject))
public class ApiResponseEncoder extends MessageToMessageEncoder<ApiResponse> {
	
	private final Json json;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ApiResponse msg, List<Object> out) throws Exception {
		if(!ctx.channel().isActive()) {
			ReferenceCountUtil.release(msg);
			throw new ClosedChannelException();
		} else if(msg.message() == null && msg.notFoundOnNull()){
			out.add(buildHttpResponse(Unpooled.EMPTY_BUFFER, HttpResponseStatus.NOT_FOUND,msg,null));
		} else if(msg.message() == null){
			out.add(buildHttpResponse(Unpooled.EMPTY_BUFFER, HttpResponseStatus.NO_CONTENT,msg,null));
		} else if(msg instanceof ApiCharSequenceResponse){
			out.add(buildHttpResponse(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(((ApiCharSequenceResponse)msg).message()), CharsetUtil.UTF_8), HttpResponseStatus.OK,msg, HttpHeaderValues.TEXT_PLAIN));
		} else if(msg instanceof ApiBufferResponse){
			out.add(buildHttpResponse((ByteBuf) ((ApiBufferResponse) msg).message(), HttpResponseStatus.OK,msg,null));
		} else {
			ApiObjectResponse response = (ApiObjectResponse) msg;
			ByteBuf buffer = ctx.alloc().buffer();
			try {
				json.serialize(response.message(), buffer);
				out.add(buildHttpResponse(buffer, HttpResponseStatus.OK,msg,HttpHeaderValues.APPLICATION_JSON));
			} catch (Throwable error) {
				buffer.release();
				throw error;
			}
		}
	}
	
	private FullHttpResponse buildHttpResponse(ByteBuf content, HttpResponseStatus status, ApiResponse msg, AsciiString contentType) {
		HttpHeaders headers = new DefaultHttpHeaders(false);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content, headers, EmptyHttpHeaders.INSTANCE);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
		if(contentType!=null) {
			headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
		}
		if(msg.isKeepAlive()) {
			headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		return response;
	}

}
