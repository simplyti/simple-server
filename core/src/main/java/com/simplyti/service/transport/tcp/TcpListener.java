package com.simplyti.service.transport.tcp;

import com.simplyti.service.transport.Listener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@EqualsAndHashCode(of = "port")
@Accessors(fluent = true)
@AllArgsConstructor
public class TcpListener implements Listener {
	
	private final int port;
	private final boolean ssl;

}
