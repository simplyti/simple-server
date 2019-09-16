package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Lifecycle {

    private HookHandler preStop;
    private HookHandler postStart;

    @CompiledJson
    public Lifecycle(final HookHandler preStop, final HookHandler postStart) {
        this.preStop = preStop;
        this.postStart = postStart;
    }

}
