package com.simplyti.server.http.api.builder;

import java.util.function.Function;

import com.simplyti.server.http.api.builder.fileupload.FileUploadApiBuilder;
import com.simplyti.server.http.api.builder.stream.StreamedRequestResponseTypableApiBuilder;
import com.simplyti.server.http.api.context.AnyWithBodyApiContext;
import com.simplyti.service.api.serializer.json.TypeLiteral;

import io.netty.util.concurrent.Future;

public interface RequestTypableApiBuilder {
	
	void then(ApiWithBodyContextConsumer consumer);
	<U> void thenFuture(Function<AnyWithBodyApiContext, Future<U>> futureSupplier);

	<T> RequestBodyTypedFinishableApiBuilder<T> withRequestType(Class<T> clazz);
	<T> RequestBodyTypedFinishableApiBuilder<T> withRequestBodyType(Class<T> clazz);
	<T> RequestBodyTypedFinishableApiBuilder<T> withRequestBodyType(TypeLiteral<T> clazz);

	<T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseType(Class<T> clazz);
	<T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseBodyType(Class<T> clazz);
	<T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseBodyType(TypeLiteral<T> clazz);

	RequestTypableApiBuilder withMaximunBodyLength(int maxBodyLength);
	RequestTypableApiBuilder withMeta(String key, String value);
	RequestTypableApiBuilder withNotFoundOnNull();

	StreamedRequestResponseTypableApiBuilder withStreamedInput();

	FileUploadApiBuilder asFileUpload();

}
