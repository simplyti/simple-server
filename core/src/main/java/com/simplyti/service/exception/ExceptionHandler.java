package com.simplyti.service.exception;

import java.io.FileNotFoundException;

import com.simplyti.service.channel.ClientChannelGroup;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ExceptionHandler {

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());

	public Future<Void> exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if (cause instanceof ServiceException) {
			return writeError(ctx, ((ServiceException) cause).status());
		} else if (cause instanceof FileNotFoundException) {
			return writeError(ctx, HttpResponseStatus.NOT_FOUND);
		} else {
			log.error("Error ocurred during service execution", cause);
			if(ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).get()) {
				return writeError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
			}else {
				return ctx.channel().newSucceededFuture();
			}
		}
	}

	private Future<Void> writeError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		return ctx.writeAndFlush(error(status));
	}

	private FullHttpResponse error(HttpResponseStatus statusCode) {
		FullHttpResponse internalServerError = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, statusCode,
				Unpooled.EMPTY_BUFFER);
		internalServerError.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
		return internalServerError;
	}

}
