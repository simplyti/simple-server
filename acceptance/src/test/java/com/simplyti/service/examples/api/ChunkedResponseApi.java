package com.simplyti.service.examples.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.simplyti.service.api.builder.ApiBuilder;
import com.simplyti.service.api.builder.ApiProvider;

public class ChunkedResponseApi implements ApiProvider{
	
	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/stream/{n}")
			.then(ctx->{
				ctx.sendChunked(s->{
					AtomicInteger i = new AtomicInteger();
					Runnable r = new Runnable() {
						@Override
						public void run() {
							int count = i.getAndIncrement();
							
							if(count<ctx.pathParamAsInt("n")) {
								s.send("Hello "+count);
								ctx.executor().schedule(this, 100, TimeUnit.MILLISECONDS);
							} else {
								s.finish();
							}
						}
					};
					ctx.executor().execute(r);
				});
			});

	}


}
