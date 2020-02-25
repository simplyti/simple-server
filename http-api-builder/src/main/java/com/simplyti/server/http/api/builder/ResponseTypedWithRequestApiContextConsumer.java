package com.simplyti.server.http.api.builder;

import java.util.function.Consumer;

import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;

public interface ResponseTypedWithRequestApiContextConsumer<T> extends Consumer<ResponseTypedWithBodyApiContext<T>> {

}
