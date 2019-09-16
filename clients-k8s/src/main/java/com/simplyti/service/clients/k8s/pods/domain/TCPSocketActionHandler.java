package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.simplyti.service.clients.k8s.json.coder.PortConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TCPSocketActionHandler {

    private final String host;
    private final Object port;

    @CompiledJson
    public TCPSocketActionHandler(String host, @JsonAttribute(converter=PortConverter.class) Object port) {
        this.host = host;
        this.port = port;
    }
}
