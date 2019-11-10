package com.simplyti.service;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import io.netty.util.ResourceLeakDetector;

@RunWith(Cucumber.class)
@CucumberOptions(
		features="classpath:features",
		snippets=SnippetType.CAMELCASE,
		plugin="pretty"
		,tags= {"@standalone"}
		)
public class StandaloneCucumberITest {
	
	@BeforeClass
	public static void prepare() throws InterruptedException {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
	}

}
