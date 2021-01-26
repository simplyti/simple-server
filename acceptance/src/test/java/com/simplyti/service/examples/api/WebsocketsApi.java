package com.simplyti.service.examples.api;

import javax.inject.Inject;

import com.simplyti.service.api.builder.ApiBuilder;

import com.simplyti.service.api.builder.ApiProvider;

import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class WebsocketsApi implements ApiProvider{
	
	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/ws")
			.then(ctx->{
					ctx.webSocket(ws->{
						ws.send("Hello!");
						ws.onMessage((String data)->{
							if(data.equals("Sayonara baby")) {
								ws.close();
							} else {
								ws.send(data);
							}
						});
					});
			});
		
	}

}
