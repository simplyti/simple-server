package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TCPSocketActionHandler implements HookHandler {

    private String host;
    private int port;

    @CompiledJson
    public TCPSocketActionHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
