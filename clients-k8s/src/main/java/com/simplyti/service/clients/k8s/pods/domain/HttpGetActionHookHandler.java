package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(fluent = true)
public class HttpGetActionHookHandler implements HookHandler {

    private final String scheme;
    private final String host;
    private final int port;
    private final String path;
    private final List<HttpHeader> headers;

    @CompiledJson
    public HttpGetActionHookHandler(
        final String scheme,
        final String host,
        final int port,
        final String path,
        final List<HttpHeader> headers) {
            this.scheme = scheme;
            this.host = host;
            this.port = port;
            this.path = path;
            this.headers = headers;
    }

}
