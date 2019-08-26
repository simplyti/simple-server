package com.simplyti.service.aws;

import com.simplyti.service.APITest;
import com.simplyti.service.aws.lambda.AWSLambda;
import com.simplyti.service.aws.lambda.AWSLambdaService;
import com.simplyti.service.builder.di.guice.ServiceBuilder;

public class APITestLamda extends AWSLambda {

	@Override
	protected AWSLambdaService build(ServiceBuilder<AWSLambdaService> builder) {
		return builder
				.withApi(APITest.class)
				.withLog4J2Logger()
				.build();
	}
	
}