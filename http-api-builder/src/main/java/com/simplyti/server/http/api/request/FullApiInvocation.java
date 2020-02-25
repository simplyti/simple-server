package com.simplyti.server.http.api.request;


import com.simplyti.server.http.api.handler.ApiInvocation;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class FullApiInvocation implements ApiInvocation{

	private final FullHttpRequest request;
	private final ApiMatchRequest match;

	public FullApiInvocation(ApiMatchRequest apiMatch, FullHttpRequest request) {
		this.match=apiMatch;
		this.request=request;
	}

	@Override
	public HttpHeaders headers() {
		return request.headers();
	}

}
