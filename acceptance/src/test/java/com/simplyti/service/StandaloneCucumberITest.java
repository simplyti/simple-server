package com.simplyti.service;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.simplyti.service.injector.SimpleObjectFactory;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import io.netty.util.ResourceLeakDetector;

@RunWith(Cucumber.class)
@CucumberOptions(
		glue = "com.simplyti.service.steps",
		features = "classpath:features",
		snippets = SnippetType.CAMELCASE,
		plugin= { "pretty" },
		tags = "@standalone"
)
public class StandaloneCucumberITest {
	
	@BeforeClass
	public static void prepare() throws InterruptedException {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
	}
	
	@AfterClass
	public static void tearDown() {
		SimpleObjectFactory.currentInjector().dispose();
	}

}
