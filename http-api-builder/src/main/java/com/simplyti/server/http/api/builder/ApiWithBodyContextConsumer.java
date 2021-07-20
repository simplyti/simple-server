package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.context.AnyWithBodyApiContext;
import com.simplyti.util.concurrent.ThrowableConsumer;

public interface ApiWithBodyContextConsumer extends ThrowableConsumer<AnyWithBodyApiContext> {

}
