package com.simplyti.service.api.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public class FileUpload extends DefaultByteBufHolder{

	private final String filename;

	public FileUpload(ByteBuf content, String filename) {
		super(content);
		this.filename=filename;
	}
	
	public String filename() {
		return filename;
	}

}
