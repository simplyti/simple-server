package com.simplyti.service;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.simplyti.service.clients.k8s.KubeClient;
import com.simplyti.service.injector.SimpleObjectFactory;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.ResourceLeakDetector;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "classpath:features",
		snippets = SnippetType.CAMELCASE,
		plugin = "pretty",
		tags = "not @standalone"
)
public class CucumberITest {
	
	@BeforeClass
	public static void prepare() throws InterruptedException {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
		KubeClient k8s = KubeClient.builder().eventLoopGroup(eventLoopGroup).server("http://localhost:8082").build();
		Awaitility.await().atMost(2,TimeUnit.MINUTES).until(()->k8sSuccess(k8s));
	}

	private static boolean k8sSuccess(KubeClient k8s) {
		try {
			return k8s.health().await().get().equals("ok");
		} catch (Exception e) {
			return false;
		}
	}
	
	@AfterClass
	public static void tearDown() {
		SimpleObjectFactory.currentInjector().dispose();
	}

}
