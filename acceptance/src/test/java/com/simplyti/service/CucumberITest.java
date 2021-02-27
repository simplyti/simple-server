package com.simplyti.service;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.simplyti.service.clients.k8s.KubeClient;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.ResourceLeakDetector;

@RunWith(Cucumber.class)
@CucumberOptions(
		features="classpath:features",
		snippets=SnippetType.CAMELCASE,
		plugin="pretty"
		,tags="not @standalone"
		)
public class CucumberITest {
	
	@BeforeClass
	public static void prepare() throws InterruptedException {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
		Awaitility.await().atMost(30, TimeUnit.SECONDS).until(()->listening(1080));
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

	private static boolean listening(int port) {
		try (Socket socket = new Socket("127.0.0.1", port)){
			return true;
			
		} catch (IOException e) {
			return false;
		}
	}

}
