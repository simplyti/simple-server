package com.simplyti.service.clients.k8s.pods.domain;

import com.dslplatform.json.CompiledJson;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors
public class ExecActionHookHandler implements HookHandler {

    private String[] command;

    @CompiledJson
    public ExecActionHookHandler(final String[] command) {
        this.command = command;
    }

}
