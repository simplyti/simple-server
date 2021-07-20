package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.RequestTypedApiContext;
import com.simplyti.util.concurrent.ThrowableConsumer;

public interface RequestTypedApiContextConsumer<T> extends ThrowableConsumer<RequestTypedApiContext<T>>{

}
