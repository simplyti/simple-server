package com.simplyti.service.aws.lambda;


import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.simplyti.service.channel.EntryChannelInit;

public class AWSLambdaModule extends AbstractModule {
	
	@Override
	public void configure() {
		bind(EntryChannelInit.class).to(AWSLambdaChannelInit.class).in(Singleton.class);
		bind(LambdaChannelPool.class).in(Singleton.class);
	}
	
}
