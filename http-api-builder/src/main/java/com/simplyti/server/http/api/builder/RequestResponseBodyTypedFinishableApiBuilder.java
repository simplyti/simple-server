package com.simplyti.server.http.api.builder;

public interface RequestResponseBodyTypedFinishableApiBuilder<T, U> {

	void then(RequestResponseTypedApiContextConsumer<T,U> consumer);

}
