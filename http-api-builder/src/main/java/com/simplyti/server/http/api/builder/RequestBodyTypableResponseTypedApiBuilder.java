package com.simplyti.server.http.api.builder;

public interface RequestBodyTypableResponseTypedApiBuilder<T> {

	<U> RequestResponseBodyTypedFinishableApiBuilder<U,T> withRequestType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<U,T> withRequestBodyType(Class<U> clazz);

	void then(ResponseTypedWithRequestApiContextConsumer<T> consumer);
	
	RequestBodyTypableResponseTypedApiBuilder<T> withNotFoundOnNull();

}
