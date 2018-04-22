package com.simplyti.service.api.shutdown;

import javax.inject.Inject;

import com.simplyti.service.Service;
import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ShutdownApi implements ApiProvider{
	
	private final Service sandbox;

	@Override
	public void build(ApiBuilder builder) {
		builder.when().get("/_shutdown")
		.then(ctx->{
			sandbox.stop();
			ctx.send(null);
		});
	}

}