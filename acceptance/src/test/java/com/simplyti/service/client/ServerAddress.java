package com.simplyti.service.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of= {"host","sni","port"})
public class ServerAddress {
	
	private final String host;
	private final String sni;
	private final int port;
	private final boolean ssl;
	
	public ServerAddress(String host,int port, boolean ssl) {
		this(host,host,port,ssl);
	}

}
