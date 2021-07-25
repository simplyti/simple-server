package com.simplyti.server.http.api.context.fileupload;

import java.util.List;

import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.util.concurrent.Future;

import io.netty.handler.codec.http.multipart.FileUpload;

public interface FileUploadAnyApiContext extends AnyApiContext {

	Future<Void> writeAndFlush(String message);
	Future<Void> writeAndFlush(Object value);
	
	Future<Void> send(String message);
	Future<Void> send(Object value);
	
	List<FileUpload> files();
	
}
