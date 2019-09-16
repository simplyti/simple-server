package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class HttpHeader {

    private final String name;
    private final String value;

    @CompiledJson
    public HttpHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
}
