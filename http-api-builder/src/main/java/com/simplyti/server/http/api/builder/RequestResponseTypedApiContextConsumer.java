package com.simplyti.server.http.api.builder;

import java.util.function.Consumer;

import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;

public interface RequestResponseTypedApiContextConsumer<T,U> extends Consumer<RequestResponseTypedApiContext<T,U>> {

}
