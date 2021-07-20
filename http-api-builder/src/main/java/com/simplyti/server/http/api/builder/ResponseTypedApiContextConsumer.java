package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.ResponseTypedApiContext;
import com.simplyti.util.concurrent.ThrowableConsumer;

public interface ResponseTypedApiContextConsumer<T> extends ThrowableConsumer<ResponseTypedApiContext<T>> {

}
