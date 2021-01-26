package com.simplyti.service.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.simplyti.service.DefaultServer;
import com.simplyti.service.Server;
import com.simplyti.service.builder.di.guice.GuiceService;
import com.simplyti.service.builder.di.guice.ServiceBuilder;

import io.vavr.control.Try;

public abstract class AWSLambda implements RequestStreamHandler {
	
	private final Server service;
	private final AWSLambdaService aws;

	public AWSLambda() {
		ServiceBuilder builder = GuiceService.builder()
			.withModule(AWSLambdaModule.class);
		this.service = Try.of(build(builder).start()::get).get();
		this.aws = this.service.instance(AWSLambdaService.class);
	}

	protected abstract DefaultServer build(ServiceBuilder builder);

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		Try.run(aws.handle(inputStream, outputStream, context)::sync);
	}
	
	public void stop() {
		Try.run(service.stop()::sync);
	}
	
}
