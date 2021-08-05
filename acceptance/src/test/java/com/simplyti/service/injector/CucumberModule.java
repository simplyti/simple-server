package com.simplyti.service.injector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class CucumberModule extends AbstractModule {
	
	private final ScenarioScope scenarioScope;

	public CucumberModule(ScenarioScope scenarioScope) {
		this.scenarioScope=scenarioScope;
	}

	@Override
	public void configure() {
		bindScope(ScenarioScoped.class, scenarioScope);
        Multibinder.newSetBinder(binder(), AutoCloseable.class);
	}

}
