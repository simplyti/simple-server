package com.simplyti.service.aws;

import com.simplyti.service.aws.lambda.AWSLambda;
import com.simplyti.service.aws.lambda.AWSLambdaService;
import com.simplyti.service.builder.di.guice.ServiceBuilder;
import com.simplyti.service.examples.api.APITest;

public class APITestLamda extends AWSLambda {

	@Override
	protected AWSLambdaService build(ServiceBuilder<AWSLambdaService> builder) {
		return builder
				.withApi(APITest.class)
				.withLog4J2Logger()
				.build();
	}
	
}