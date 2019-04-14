package com.simplyti.service.exception;

import java.io.FileNotFoundException;

import javax.ws.rs.WebApplicationException;

import com.simplyti.service.channel.ClientChannelGroup;
import com.simplyti.service.jaxrs.SimpleResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class ExceptionHandler {

	private final InternalLogger log = InternalLoggerFactory.getInstance(getClass());

	public Future<Void> exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		if(cause instanceof WebApplicationException) {
			SimpleResponse response = (SimpleResponse) ((WebApplicationException) cause).getResponse();
			return writeResponse(ctx, response.responseStatus(),response.headers());
		} else if (cause instanceof ServiceException) {
			return writeResponse(ctx, ((ServiceException) cause).status(),null);
		} else if (cause instanceof FileNotFoundException) {
			return writeResponse(ctx, HttpResponseStatus.NOT_FOUND,null);
		} else if (cause instanceof DecoderException) {
			return  writeResponse(ctx, HttpResponseStatus.BAD_REQUEST,null);
		} else {
			log.error("Error ocurred during service execution", cause);
			if(ctx.channel().attr(ClientChannelGroup.IN_PROGRESS).get()) {
				return writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR,null);
			}else {
				return ctx.channel().newSucceededFuture();
			}
		}
	}

	private Future<Void> writeResponse(ChannelHandlerContext ctx, HttpResponseStatus status, HttpHeaders headers) {
		return ctx.writeAndFlush(response(status,headers));
	}

	private FullHttpResponse response(HttpResponseStatus statusCode, HttpHeaders headers) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, statusCode,
				Unpooled.EMPTY_BUFFER);
		if(headers!=null) {
			response.headers().add(headers);
		}
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
		return response;
	}

}
