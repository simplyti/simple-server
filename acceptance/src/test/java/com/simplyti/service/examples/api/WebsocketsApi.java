package com.simplyti.service.examples.api;

import javax.inject.Inject;

import com.simplyti.service.api.builder.ApiBuilder;

import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.examples.clients.telnet.TelnetClient;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class WebsocketsApi implements ApiProvider{
	
	private final TelnetClient client;
	
	@Override
	public void build(ApiBuilder builder) {
		
		builder.when().get("/ws")
			.then(ctx->{
				client.request()
					.withEndpoint("localhost",7067)
					.open(conn -> {
						ctx.webSocket(ws->{
							conn.onData(data->{
								ws.send(data.retain());
							});
							ws.onData((ByteBuf data)->{
								conn.send(data.retain());
							});
						});
						ctx.channel().closeFuture().addListener(f->conn.close());
					});
			});
		
	}

}
