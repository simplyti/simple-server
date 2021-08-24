package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.util.concurrent.ThrowableConsumer;

public interface ApiContextConsumer extends ThrowableConsumer<AnyApiContext> {

}
