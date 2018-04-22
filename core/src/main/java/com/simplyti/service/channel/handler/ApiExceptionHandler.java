package com.simplyti.service.channel.handler;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import com.simplyti.service.exception.ServiceException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.AllArgsConstructor;

@Sharable
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ApiExceptionHandler extends ChannelInboundHandlerAdapter {
	
	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof ServiceException) {
			writeError(ctx,((ServiceException) cause).status());
		} else if (cause instanceof FileNotFoundException) {
			writeError(ctx,HttpResponseStatus.NOT_FOUND);
		}else{
			log.error("Error ocurred during service execution", cause);
			writeError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void writeError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		ctx.writeAndFlush(error(status));
	}

	private FullHttpResponse error(HttpResponseStatus statusCode) {
		FullHttpResponse internalServerError = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,statusCode,Unpooled.EMPTY_BUFFER);
		internalServerError.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
		return internalServerError;
	}
	
}
