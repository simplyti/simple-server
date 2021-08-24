package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.RequestResponseTypedApiContext;
import com.simplyti.util.concurrent.ThrowableConsumer;

public interface RequestResponseTypedApiContextConsumer<T,U> extends ThrowableConsumer<RequestResponseTypedApiContext<T,U>> {

}
