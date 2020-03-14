package com.simplyti.service.examples.api;


import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

public class StreamAPITest implements ApiProvider{
	

	@Override
	public void build(ApiBuilder builder) {
		builder.when().post("/streamed")
			.withStreamedInput()
			.then(ctx->{
				HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
				response.headers().set(HttpHeaderNames.CONTENT_LENGTH,ctx.request().headers().get(HttpHeaderNames.CONTENT_LENGTH));
				ctx.send(response);
				ctx.stream(content->ctx.send(new DefaultHttpContent(content).retain()))
					.addListener(f->ctx.send(LastHttpContent.EMPTY_LAST_CONTENT));
			});
	}

}
