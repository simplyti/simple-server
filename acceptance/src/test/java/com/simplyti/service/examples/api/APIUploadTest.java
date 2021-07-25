package com.simplyti.service.examples.api;

import java.util.List;
import java.util.stream.Collectors;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.handler.codec.http.multipart.FileUpload;

public class APIUploadTest implements ApiProvider{

	@Override
	public void build(ApiBuilder builder) {
		builder.when().post("/upload")
			.asFileUpload()
			.then(ctx->{
				List<String> list = ctx.files().stream()
					.filter(p->p instanceof FileUpload)
					.map(FileUpload.class::cast)
					.map(file->file.getName())
					.collect(Collectors.toList());
				ctx.send("Got "+list);
			});
	}

}
