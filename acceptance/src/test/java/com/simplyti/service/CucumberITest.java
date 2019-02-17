package com.simplyti.service;

import java.io.IOException;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.jayway.awaitility.Awaitility;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import io.netty.util.ResourceLeakDetector;

@RunWith(Cucumber.class)
@CucumberOptions(
		features="classpath:features",
		snippets=SnippetType.CAMELCASE,
		plugin="pretty"
		)
public class CucumberITest {
	
	@BeforeClass
	public static void prepare() throws InterruptedException {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
		Awaitility.await().until(()->listening(1080));
	}

	private static boolean listening(int port) {
		try (Socket socket = new Socket("127.0.0.1", port)){
			return true;
			
		} catch (IOException e) {
			return false;
		}
	}

}
