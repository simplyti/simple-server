package com.simplyti.service.api.filter;

import com.simplyti.service.api.ApiOperation;

import io.netty.handler.codec.http.HttpHeaders;

public interface FilterContext {

	public void done();

	public HttpHeaders headers();

	public void fail(Throwable unauthorizedException);

	public ApiOperation<?, ?> operation();

}
