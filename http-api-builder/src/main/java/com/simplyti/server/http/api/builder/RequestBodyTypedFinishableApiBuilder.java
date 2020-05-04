package com.simplyti.server.http.api.builder;

import com.simplyti.service.api.serializer.json.TypeLiteral;

public interface RequestBodyTypedFinishableApiBuilder<T> {
	
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseBodyType(Class<U> clazz);
	<U> RequestResponseBodyTypedFinishableApiBuilder<T,U> withResponseType(TypeLiteral<U> type);
	
	void then(RequestTypedApiContextConsumer<T> consumer);


}
