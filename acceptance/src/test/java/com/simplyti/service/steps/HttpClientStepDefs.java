package com.simplyti.service.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import com.jayway.awaitility.Awaitility;
import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.proxy.ProxiedEndpoint;
import com.simplyti.service.clients.proxy.Proxy;
import com.simplyti.service.clients.proxy.Proxy.ProxyType;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.vavr.control.Try;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class HttpClientStepDefs {
	
	private static final Endpoint LOCAL_ENDPOINT = HttpEndpoint.of("http://localhost:8080");

	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	@Named("scenario")
	private HttpClient sutClient;
	
	@Inject
	@Named("singleton")
	private HttpClient client;
	
	
	@Before
	public void checkProxy() {
		HttpEndpoint target = HttpEndpoint.of("http://httpbin:8080/status/200");
		ProxiedEndpoint endpoint = ProxiedEndpoint.of(target).through("127.0.0.1", 3128, Proxy.ProxyType.HTTP);
		Awaitility.await().until(()->{
			Try<FullHttpResponse> result = Try.of(()->client
					.withEndpoin(endpoint)
					.withReadTimeout(1000)
					.get(target.path())
					.fullResponse().get());
			if(result.isSuccess()) {
				result.get().release();
				return true;
			}
			return false;
		});
	}
	
	@When("^I get \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithClientGettingResponse(String path, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.withEndpoin(LOCAL_ENDPOINT)
			.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@Then("^I check that http client has (\\d+) iddle connection$")
	public void iCheckThatHttpClientHasIddleConnection(int number) throws Exception {
		assertThat(sutClient.monitor().iddleConnections(),equalTo(number));
	}
	
	@Then("^I check that http client has (\\d+) active connection$")
	public void iCheckThatHttpClientHasActiveConnection(int number) throws Exception {
		Awaitility.await()
		.pollDelay(50, TimeUnit.MILLISECONDS)
		.until(sutClient.monitor()::activeConnections,equalTo(number));
	}
	
	@Then("^I check that http client has (\\d+) total connection$")
	public void iCheckThatHttpClientHasTotalConnection(int number) throws Exception {
		assertThat(sutClient.monitor().totalConnections(),equalTo(number));
	}
	
	@When("^I get \"([^\"]*)\" to port (\\d+) getting response \"([^\"]*)\"$")
	public void iGetToPortGettingResponse(String path, int port, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.withEndpoin(new Endpoint(HttpEndpoint.HTTP_SCHEMA, new Address(LOCAL_ENDPOINT.address().host(), port)))
				.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithClientGetingResponse(String path,String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.withEndpoin(LOCAL_ENDPOINT)
			.post(path)
			.body(alloc->alloc.buffer().writeBytes(Unpooled.copiedBuffer(body, CharsetUtil.UTF_8)))
			.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and response time (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyAndResponseTimeGettingResponse(String path, String body, int timeout, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.withEndpoin(LOCAL_ENDPOINT)
				.withReadTimeout(timeout)
				.post(path)
				.body(alloc->alloc.buffer().writeBytes(Unpooled.copiedBuffer(body, CharsetUtil.UTF_8)))
				.fullResponse();
			scenarioData.put(resultKey, response);
	}
	
	@Then("^I check that http response \"([^\"]*)\" has body \"([^\"]*)\"$")
	public void iCheckThatHttpResponseHasBody(String key, String body) throws Exception {
		@SuppressWarnings("unchecked")
		Future<FullHttpResponse> future = (Future<FullHttpResponse>) scenarioData.get(key);
		FullHttpResponse response = future.get();
		assertThat(response.content().toString(CharsetUtil.UTF_8),equalTo(body));
		response.release();
	}
	
	@Then("^I check that http response \"([^\"]*)\" has status code (\\d+)$")
	public void iCheckThatHttpResponseHasStatusCode(String key, int status) throws Exception {
		@SuppressWarnings("unchecked")
		Future<FullHttpResponse> future = (Future<FullHttpResponse>) scenarioData.get(key);
		FullHttpResponse response = future.get();
		assertThat(response.status().code(),equalTo(status));
	}
	

	@Then("^I get url \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetUrlGettingResponse(String endpointUrl, String resultKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		Future<FullHttpResponse> response = sutClient.withEndpoin(endpoint).get(endpoint.path()).fullResponse();
		scenarioData.put(resultKey, response);
	}

	@Given("^\"([^\"]*)\" proxy \"([^\"]*)\" as \"([^\"]*)\"$")
	public void socksProxyAs(ProxyType type, String host, String key) throws Exception {
		String[] hostPort = host.split(":");
	    Proxy proxy = new Proxy(hostPort[0], Integer.parseInt(hostPort[1]), type);
	    scenarioData.put(key, proxy);
	}
	
	@When("^I get url \"([^\"]*)\" through proxy \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostUrlWithDataThroughProxyGettingResponse(String endpointUrl, String proxyKey, String resultKey) throws Exception {
		Proxy proxy = (Proxy) scenarioData.get(proxyKey);
		HttpEndpoint target = HttpEndpoint.of(endpointUrl);
		ProxiedEndpoint endpoint;
		switch(proxy.type()) {
		case SOCKS5:
			endpoint = ProxiedEndpoint.of(target).throughSocks5(proxy.address().host(), proxy.address().port());
			break;
		case HTTP:
		default:
			endpoint = ProxiedEndpoint.of(target).throughHTTP(proxy.address().host(), proxy.address().port());
			break;
		}
		Future<FullHttpResponse> response = sutClient.withEndpoin(endpoint).get(target.path()).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@Then("^I get url \"([^\"]*)\" getting http objects \"([^\"]*)\"$")
	public void iGetUrlGettingHttpResponse(String endpointUrl, String resultKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		List<HttpObject> objets = new ArrayList<>();
		sutClient.withEndpoin(endpoint).get(endpoint.path())
				.forEach(objets::add).await();
		scenarioData.put(resultKey, objets);
	}
	
	@Then("^I check that http objects \"([^\"]*)\" contains (\\d+) items$")
	public void iCheckThatHttpObjectsContainsItems(String key, int number) throws Exception {
		@SuppressWarnings("unchecked")
		List<HttpObject> objects = (List<HttpObject>) scenarioData.get(key);
		assertThat(objects,hasSize(number));
	}
	
	@When("^I get url \"([^\"]*)\" getting stream \"([^\"]*)\"$")
	public void iGetUrlGettingStream(String endpointUrl, String resultKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		List<ByteBuf> stream = new ArrayList<>();
		sutClient.withEndpoin(endpoint).get(endpoint.path())
				.stream(stream::add).await();
		scenarioData.put(resultKey, stream);
	}

	@Then("^I check that stream \"([^\"]*)\" contains (\\d+) items$")
	public void iCheckThatStreamContainsItems(String key, int number) throws Exception {
		@SuppressWarnings("unchecked")
		List<ByteBuf> objects = (List<ByteBuf>) scenarioData.get(key);
		assertThat(objects,hasSize(number));
	}
	
}
