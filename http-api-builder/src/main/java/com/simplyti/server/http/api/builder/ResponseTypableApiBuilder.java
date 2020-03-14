package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.builder.sse.ServerSentEventApiBuilder;
import com.simplyti.service.api.serializer.json.TypeLiteral;

public interface ResponseTypableApiBuilder {
	
	void then(ApiContextConsumer consumer);
	
	<T> ResponseBodyTypedApiBuilder<T> withResponseType(Class<T> clazz);
	<T> ResponseBodyTypedApiBuilder<T> withResponseType(TypeLiteral<T> clazz);
	
	<T> ResponseBodyTypedApiBuilder<T> withResponseBodyType(Class<T> clazz);
	<T> ResponseBodyTypedApiBuilder<T> withResponseBodyType(TypeLiteral<T> clazz);

	ResponseTypableApiBuilder withMeta(String key, String value);

	ServerSentEventApiBuilder asServerSentEvent();
	
}
