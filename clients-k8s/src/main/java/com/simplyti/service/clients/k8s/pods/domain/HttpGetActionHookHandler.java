package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.simplyti.service.clients.k8s.json.coder.PortConverter;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(fluent = true)
public class HttpGetActionHookHandler {

    private final String scheme;
    private final String host;
    private final Object port;
    private final String path;
    private final List<HttpHeader> headers;

    @CompiledJson
    public HttpGetActionHookHandler(
        final String scheme,
        final String host,
        @JsonAttribute(converter=PortConverter.class) final Object port,
        final String path,
        final List<HttpHeader> headers) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
            this.path = path;
            this.headers = headers;
    }

}
