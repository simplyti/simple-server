package com.simplyti.server.http.api.builder;

import com.simplyti.server.http.api.builder.fileupload.FileUploadApiBuilder;
import com.simplyti.server.http.api.builder.stream.StreamedRequestResponseTypableApiBuilder;

public interface RequestTypableApiBuilder {
	
	void then(ApiWithBodyContextConsumer consumer);

	<T> RequestBodyTypedFinishableApiBuilder<T> withRequestType(Class<T> clazz);
	<T> RequestBodyTypedFinishableApiBuilder<T> withRequestBodyType(Class<T> clazz);

	<T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseType(Class<T> clazz);
	<T> RequestBodyTypableResponseTypedApiBuilder<T> withResponseBodyType(Class<T> clazz);

	RequestTypableApiBuilder withMaximunBodyLength(int maxBodyLength);
	RequestTypableApiBuilder withMeta(String key, String value);
	RequestTypableApiBuilder withNotFoundOnNull();

	StreamedRequestResponseTypableApiBuilder withStreamedInput();

	FileUploadApiBuilder asFileUpload();

}
