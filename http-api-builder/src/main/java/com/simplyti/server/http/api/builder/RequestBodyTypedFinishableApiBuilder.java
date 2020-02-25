package com.simplyti.server.http.api.builder;

public interface RequestBodyTypedFinishableApiBuilder<T> {
	
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseBodyType(Class<U> clazz);
	
	void then(RequestTypedApiContextConsumer<T> consumer);


}
