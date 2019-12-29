package com.simplyti.service.clients.http.stream.request;

import com.simplyti.service.clients.http.request.BaseFinishableHttpRequestBuilder;
import com.simplyti.util.concurrent.Future;

import io.netty.handler.codec.http.HttpContent;

public interface StreamedInputHttpRequestBuilder extends BaseFinishableHttpRequestBuilder<StreamedInputHttpRequestBuilder> {

	Future<Void> send(HttpContent data);

}
