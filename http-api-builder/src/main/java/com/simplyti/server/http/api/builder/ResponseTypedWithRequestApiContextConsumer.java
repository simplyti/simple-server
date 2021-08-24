package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.ResponseTypedWithBodyApiContext;
import com.simplyti.util.concurrent.ThrowableConsumer;

public interface ResponseTypedWithRequestApiContextConsumer<T> extends ThrowableConsumer<ResponseTypedWithBodyApiContext<T>> {

}
