package com.simplyti.server.http.api.context.fileupload;

import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.util.concurrent.Future;
import com.simplyti.util.concurrent.ThrowableConsumer;

import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public interface FileUploadAnyApiContext extends AnyApiContext {

	Future<Void> writeAndFlush(String message);
	Future<Void> writeAndFlush(Object value);
	
	Future<Void> send(String message);
	Future<Void> send(Object value);

	Future<Void> eachPart(ThrowableConsumer<InterfaceHttpData> consumer);

}
