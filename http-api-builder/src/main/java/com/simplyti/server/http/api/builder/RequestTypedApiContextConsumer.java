package com.simplyti.server.http.api.builder;

import java.util.function.Consumer;

import com.simplyti.server.http.api.context.RequestTypedApiContext;

public interface RequestTypedApiContextConsumer<T> extends Consumer<RequestTypedApiContext<T>>{

}
