package com.simplyti.server.http.api.builder;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.sse.ServerSentEventApiBuilder;
import com.simplyti.server.http.api.context.AnyApiContext;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.util.concurrent.Future;

public interface ResponseTypableApiBuilder {
	
	void then(ApiContextConsumer consumer);
	
	<T> void thenFuture(Function<AnyApiContext,Future<T>> object);
	
	<T> ResponseBodyTypedApiBuilder<T> withResponseType(Class<T> clazz);
	<T> ResponseBodyTypedApiBuilder<T> withResponseType(TypeLiteral<T> clazz);
	
	<T> ResponseBodyTypedApiBuilder<T> withResponseBodyType(Class<T> clazz);
	<T> ResponseBodyTypedApiBuilder<T> withResponseBodyType(TypeLiteral<T> clazz);

	ResponseTypableApiBuilder withMeta(String key, String value);
	ResponseTypableApiBuilder withNotFoundOnNull();

	ServerSentEventApiBuilder asServerSentEvent();

}
