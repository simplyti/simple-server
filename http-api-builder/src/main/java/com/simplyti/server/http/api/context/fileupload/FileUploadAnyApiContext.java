package com.simplyti.server.http.api.context.fileupload;

import java.util.Collection;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.context.WithBodyApiContext;
import com.simplyti.server.http.api.fileupload.FileUpload;
import com.simplyti.util.concurrent.Future;

public interface FileUploadAnyApiContext extends WithBodyApiContext, ApiContext {

	Collection<FileUpload> body();
	
	Future<Void> writeAndFlush(String message);
	Future<Void> writeAndFlush(Object value);
	
	Future<Void> send(String message);
	Future<Void> send(Object value);

}
