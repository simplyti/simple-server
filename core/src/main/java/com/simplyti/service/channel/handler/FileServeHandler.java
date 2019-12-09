package com.simplyti.service.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.simplyti.service.fileserver.DirectoryResolver;
import com.simplyti.service.fileserver.FileServe;
import com.simplyti.service.fileserver.FileServeConfiguration;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

@Sharable
public class FileServeHandler extends SimpleChannelInboundHandler<HttpRequest> {
	
	private final Pattern pattern;
	private final DirectoryResolver directoryResolver;
	private final FileServe fileServer;
	
	@Inject
	public FileServeHandler(FileServeConfiguration fileServerConfig,FileServe fileServer) {
		this.pattern=fileServerConfig.pattern();
		this.directoryResolver=fileServerConfig.directoryResolver();
		this.fileServer=fileServer;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
		if (request.method() != HttpMethod.GET) {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
			ctx.writeAndFlush(response);
			return;
		}
		
		Matcher matcher = pattern.matcher(new QueryStringDecoder(request.uri()).path());
		matcher.matches();
		String path = directoryResolver.resolve(matcher) + File.separatorChar + matcher.group(1);
		fileServer.serve(path, ctx.channel(), request);
	}

}
