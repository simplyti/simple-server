package com.simplyti.server.http.api.builder;

import java.util.function.Function;

import com.simplyti.server.http.api.context.RequestTypedApiContext;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.util.concurrent.Future;

public interface RequestBodyTypedFinishableApiBuilder<T> {
	
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseBodyType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseBodyType(TypeLiteral<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseType(TypeLiteral<U> type);
	
	void then(RequestTypedApiContextConsumer<T> consumer);
	<U> void thenFuture(Function<RequestTypedApiContext<T>,Future<U>> object);
	
	RequestBodyTypedFinishableApiBuilder<T> withMeta(String key, Object value);
	RequestBodyTypedFinishableApiBuilder<T> withNotFoundOnNull();


}
