package com.simplyti.service.injector;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.HashMap;
import java.util.Map;

public class ScenarioScope implements Scope {

    private Map<Key<?>, Object> scenarioValues = null;

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return () -> {
            if (scenarioValues == null) {
                throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
            }

            @SuppressWarnings("unchecked")
            T current = (T) scenarioValues.get(key);
            if (current == null && !scenarioValues.containsKey(key)) {
                current = unscoped.get();
                scenarioValues.put(key, current);
            }
            return current;
        };
    }

    public void enterScope() {
        checkState(scenarioValues == null, "A scoping block is already in progress");
        scenarioValues = new HashMap<>();
    }

    public void exitScope() {
        checkState(scenarioValues != null, "No scoping block in progress");
        scenarioValues = null;
    }

    private void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }

}
