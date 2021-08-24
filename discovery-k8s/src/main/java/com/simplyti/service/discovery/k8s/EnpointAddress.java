package com.simplyti.service.discovery.k8s;


import com.simplyti.service.clients.endpoint.TcpAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class EnpointAddress {
	
	private final String portName;
	private final TcpAddress address;

}
