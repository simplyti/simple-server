package com.simplyti.server.http.api.builder.stream;

import java.util.function.Consumer;

import com.simplyti.server.http.api.context.chunked.ChunkedResponseContext;

public interface ChunkedResponseContextConsumer extends Consumer<ChunkedResponseContext> {

}
