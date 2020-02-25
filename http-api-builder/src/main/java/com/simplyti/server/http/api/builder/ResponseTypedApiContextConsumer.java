package com.simplyti.server.http.api.builder;

import java.util.function.Consumer;

import com.simplyti.server.http.api.context.ResponseTypedApiContext;

public interface ResponseTypedApiContextConsumer<T> extends Consumer<ResponseTypedApiContext<T>> {

}
