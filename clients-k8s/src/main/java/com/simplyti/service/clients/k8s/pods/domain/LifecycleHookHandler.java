package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
public class LifecycleHookHandler {
	
	private final ExecActionHookHandler exec;
	private final HttpGetActionHookHandler httpGet;
	private final TCPSocketActionHandler tcpSocket;
	
	@CompiledJson
	public LifecycleHookHandler(ExecActionHookHandler exec,
			HttpGetActionHookHandler httpGet,
			TCPSocketActionHandler tcpSocket) {
		this.exec=exec;
		this.httpGet=httpGet;
		this.tcpSocket=tcpSocket;
	}
}
