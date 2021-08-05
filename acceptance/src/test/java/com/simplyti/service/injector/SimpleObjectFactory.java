package com.simplyti.service.injector;

import java.util.Set;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;

import io.cucumber.core.backend.ObjectFactory;
import lombok.SneakyThrows;

public class SimpleObjectFactory implements ObjectFactory {
	
	private static SimpleObjectFactory INSTANCE;
	
	private final Injector injector;
	private final ScenarioScope scenarioScope;

	public SimpleObjectFactory() {
		this.scenarioScope = new ScenarioScope();
		this.injector =  Guice.createInjector( Stage.PRODUCTION , new CucumberModule(scenarioScope), new AcceptanceModule());
		INSTANCE = this;
	}

	@Override
	public boolean addClass(Class<?> glueClass) {
		return true;
	}

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return this.injector.getInstance( clazz );
	}

	@Override
	public void start() {
		scenarioScope.enterScope();
	}

	@Override
	public void stop() {
		scenarioScope.exitScope();
	}

	public static SimpleObjectFactory currentInjector() {
		return INSTANCE;
	}

	public void dispose() {
		this.injector.getInstance(Key.get(new TypeLiteral<Set<AutoCloseable>>() {}))
			.forEach(this::close);
	}
	
	@SneakyThrows
	private void close(AutoCloseable close) {
		close.close();
	}

}
