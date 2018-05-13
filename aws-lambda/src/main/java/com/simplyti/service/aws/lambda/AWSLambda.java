package com.simplyti.service.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.simplyti.service.Service;
import com.simplyti.service.builder.ServiceBuilder;

import io.vavr.control.Try;

public abstract class AWSLambda implements RequestStreamHandler {
	
	private final AWSLambdaService service;

	public AWSLambda() {
		ServiceBuilder<AWSLambdaService> builder = Service.builder(AWSLambdaService.class)
			.withModule(AWSLambdaModule.class);
		this.service = Try.of(build(builder).start()::get).get();
	}

	protected abstract AWSLambdaService build(ServiceBuilder<AWSLambdaService> builder);

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		Try.run(service.handle(inputStream, outputStream, context)::sync);
	}
	
	public void stop() {
		Try.run(service.stop()::sync);
	}
	
}
