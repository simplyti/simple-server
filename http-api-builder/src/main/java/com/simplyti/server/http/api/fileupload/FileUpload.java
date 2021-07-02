package com.simplyti.server.http.api.fileupload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class FileUpload extends DefaultByteBufHolder{

	private final String filename;
	private final String name;

	public FileUpload(String name, ByteBuf content, String filename) {
		super(content);
		this.filename=filename;
		this.name=name;
	}
	
}
