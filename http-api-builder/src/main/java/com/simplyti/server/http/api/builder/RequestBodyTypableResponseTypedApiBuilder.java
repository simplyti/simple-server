package com.simplyti.server.http.api.builder;

import com.simplyti.service.api.serializer.json.TypeLiteral;

public interface RequestBodyTypableResponseTypedApiBuilder<T> {

	<U> RequestResponseBodyTypedFinishableApiBuilder<U,T> withRequestType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<U,T> withRequestType(TypeLiteral<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<U,T> withRequestBodyType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<U,T> withRequestBodyType(TypeLiteral<U> clazz);

	void then(ResponseTypedWithRequestApiContextConsumer<T> consumer);
	
	RequestBodyTypableResponseTypedApiBuilder<T> withMaximunBodyLength(int maxBodyLength);
	RequestBodyTypableResponseTypedApiBuilder<T> withMeta(String key, String value);
	RequestBodyTypableResponseTypedApiBuilder<T> withNotFoundOnNull();

}
