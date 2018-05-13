package com.simplyti.service.aws.lambda;

import javax.inject.Inject;

import com.simplyti.service.aws.lambda.coders.AwsHttpLambdaRequestDecoder;
import com.simplyti.service.aws.lambda.coders.AwsHttpResponseEncoder;
import com.simplyti.service.channel.EntryChannelInit;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.json.JsonObjectDecoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor=@__(@Inject))
public class AWSLambdaChannelInit implements EntryChannelInit{
	
	private final AwsHttpLambdaRequestDecoder awsHttpLambdaRequestDecoder;
	private final AwsHttpResponseEncoder awsHttpResponseEncoder;

	@Override
	public void init(ChannelPipeline pipeline) {
		pipeline.addLast(awsHttpResponseEncoder);
		pipeline.addLast(new HttpObjectAggregator(10000000));
		pipeline.addLast(new JsonObjectDecoder());
		pipeline.addLast(awsHttpLambdaRequestDecoder);
	}

}
