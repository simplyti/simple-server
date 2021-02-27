package com.simplyti.service.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.awaitility.Awaitility;

import com.simplyti.service.Server;
import com.simplyti.service.clients.GenericClient;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.gateway.GatewayConfig;
import com.simplyti.service.gateway.http.HttpGatewayClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;

public class GatewayStepDefs {
	
	@Inject
	@Named("singleton")
	private HttpClient http;
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Given("^gateway config \"([^\"]*)\" with monitor enabled$")
	public void gatewayConfigWithMonitorEnabled(String key) throws Exception {
		scenarioData.put(key, GatewayConfig.builder().clientMonitorEnabled(true).build());
	}
	
	@Then("^I check that gateway \"([^\"]*)\" client has (\\d+) iddle connection$")
	public void iCheckThatGatewayClientHasIddleConnection(String gatewayKey, int count) throws Exception {
		Future<?> futureGateway = (Future<?>) scenarioData.get(gatewayKey);
		Server server = (Server) futureGateway.getNow();
		GenericClient gatewayClient = server.instanceAnnotatedWith(GenericClient.class,HttpGatewayClient.class);
		assertThat(gatewayClient.monitor().idleConnections(), equalTo(count));
	}
	
	@Then("^I check that gateway \"([^\"]*)\" client has multiple iddle connections$")
	public void iCheckThatGatewayClientHasMultipleIddleConnections(String gatewayKey) throws Exception {
		Future<?> futureGateway = (Future<?>) scenarioData.get(gatewayKey);
		Server server = (Server) futureGateway.getNow();
		GenericClient gatewayClient = server.instanceAnnotatedWith(GenericClient.class,HttpGatewayClient.class);
		assertThat(gatewayClient.monitor().idleConnections(), greaterThan(1));
	}
	
	@Then("^I check that gateway \"([^\"]*)\" client has (\\d+) active connection$")
	public void iCheckThatGatewayClientHasActiveConnection(String gatewayKey, int count) throws Exception {
		Future<?> futureGateway = (Future<?>) scenarioData.get(gatewayKey);
		Server server = (Server) futureGateway.getNow();
		GenericClient gatewayClient = server.instanceAnnotatedWith(GenericClient.class,HttpGatewayClient.class);
		Awaitility.await()
			.pollDelay(50, TimeUnit.MILLISECONDS)
			.until(gatewayClient.monitor()::activeConnections,equalTo(count));
	}
	
	@When("^I post \"([^\"]*)\" with (\\d+\\.\\d+) mb of payload getting response \"([^\"]*)\"$")
	public void iPostWithMbOfPayloadGettingResponse(String path, double mb, String key) throws Exception {
		com.simplyti.util.concurrent.Future<FullHttpResponse> result = http.request()
			.withEndpoint("localhost", 8080)
			.post(path)
			.withBodyWriter(b -> writeMegabytes(b,mb))
			.fullResponse();
		scenarioData.put(key, result);
	}

	private void writeMegabytes(ByteBuf b, double mb) {
		Random r = new Random();
		byte[] buff = new byte[1000000];
		int bytesToSend = (int) (mb * buff.length);
		while(bytesToSend>0) {
			r.nextBytes(buff);
			int length = Math.min(buff.length, bytesToSend);
			b.writeBytes(buff, 0, length);
			bytesToSend-=length;
		}
	}

	@Then("^I check that http response \"([^\"]*)\" has a body with size of (\\d+\\.\\d+) mb$")
	public void iCheckThatHttpResponseHasABodyWithSizeOfMb(String key, double size) throws Exception {
		@SuppressWarnings("unchecked")
		Future<FullHttpResponse> future = (Future<FullHttpResponse>) scenarioData.get(key);
		FullHttpResponse response = future.get();
		assertThat(response.content().readableBytes(),equalTo((int) (size * 1000000)));
		response.release();
	}

}
