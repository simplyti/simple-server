package com.simplyti.service.fileserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import javax.activation.MimetypesFileTypeMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedFile;

public class FileServe {

	private static final int HTTP_CACHE_SECONDS = 10;

	private final MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	public void serve(String path, ChannelHandlerContext ctx, HttpRequest request) throws IOException {
		File file = new File(path);
		if (file.isHidden() || !file.exists()) {
			throw new FileNotFoundException();
		}
		
		String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            TemporalAccessor ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);
            long ifModifiedSinceDateSeconds = Instant.from(ifModifiedSinceDate).getEpochSecond();
            long fileLastModifiedSeconds = Instant.ofEpochMilli(file.lastModified()).getEpochSecond();
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
            		sendStatus(ctx,HttpResponseStatus.NOT_MODIFIED);
                return;
            }
        }
		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, fileLength);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
        ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("GMT"));
		response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(now));
        response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(now.plusSeconds(HTTP_CACHE_SECONDS)));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        ZonedDateTime lastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("GMT"));
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(lastModified));
        
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
		ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0,  fileLength, 8192)), ctx.newProgressivePromise());
	}
	
	private void sendStatus(ChannelHandlerContext ctx, HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
		ctx.writeAndFlush(response);
	}
	
}
