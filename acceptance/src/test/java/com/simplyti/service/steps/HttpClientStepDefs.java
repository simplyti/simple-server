package com.simplyti.service.steps;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.awaitility.Awaitility;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.simplyti.service.clients.Client;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.endpoint.ssl.SSLEndpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.http.request.ChunckedBodyRequest;
import com.simplyti.service.clients.http.websocket.WebsocketClient;
import com.simplyti.service.clients.proxy.ProxiedEndpoint;
import com.simplyti.service.clients.proxy.ProxiedEndpointBuilder;
import com.simplyti.service.clients.proxy.Proxy;
import com.simplyti.service.clients.proxy.Proxy.ProxyType;
import com.simplyti.util.concurrent.Future;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class HttpClientStepDefs {
	
	private static final Endpoint LOCAL_ENDPOINT = HttpEndpoint.of("http://localhost:8080");
	
	@Inject
	private EventLoopGroup eventLoopGroup;

	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	@Named("scenario")
	private HttpClient sutClient;
	
	@Inject
	@Named("singleton")
	private HttpClient client;
	
	@Given("^a single thread event loop group \"([^\"]*)\"$")
	public void aSingleThreadEventLoopGroup(String key) throws Exception {
		scenarioData.put(key, eventLoopGroup.next());
	}
	
	@When("^I create an http client \"([^\"]*)\" with event loop group \"([^\"]*)\"$")
	public void iCreateAnHttpClientWithEventLoopGroup(String key, String eventloopKey) throws Exception {
		EventLoopGroup eventloop = (EventLoopGroup) scenarioData.get(eventloopKey);
		scenarioData.put(key,HttpClient.builder()
			.withEventLoopGroup(eventloop)
			.withMonitorEnabled()
			.withCheckStatusCode()
			.build());
	}
	
	@When("^I create an http client \"([^\"]*)\" with pool size (\\d+)$")
	public void iCreateAnHttpClientWithPoolSize(String key, int poolSize) throws Exception {
		scenarioData.put(key,HttpClient.builder()
			.withEventLoopGroup(eventLoopGroup)
			.withChannelPoolSize(poolSize)
			.withMonitorEnabled()
			.withCheckStatusCode()
			.build());
	}
	
	@When("^I create an http client \"([^\"]*)\" with unpooled channels$")
	public void iCreateAnHttpClientWithUnpooledChannels(String key) throws Exception {
		scenarioData.put(key,HttpClient.builder()
				.withEventLoopGroup(eventLoopGroup)
				.withUnpooledChannels()
				.withMonitorEnabled()
				.withCheckStatusCode()
				.build());
	}
	
	@When("^I create an http client \"([^\"]*)\" with pool idle timeout (\\d+)$")
	public void iCreateAnGenericClientWithPoolIdleTimeout(String key, int iddleTime) throws Exception {
		scenarioData.put(key,HttpClient.builder()
	    	.withEventLoopGroup(eventLoopGroup)
	    	.withMonitorEnabled()
	    	.withChannelPoolIdleTimeout(iddleTime)
	    	.build());
	}
	
	@When("^I create an http client \"([^\"]*)\" with read timeout (\\d+)$")
	public void iCreateAnHttpClientWithReadTimeout(String key, int timeoutMilis) throws Exception {
		scenarioData.put(key,HttpClient.builder()
		    	.withEventLoopGroup(eventLoopGroup)
		    	.withMonitorEnabled()
		    	.withReadTimeout(timeoutMilis)
		    	.build());
	}
	
	@When("^I create an http client \"([^\"]*)\" with basic auth \"([^\"]*)\" \"([^\"]*)\"$")
	public void iCreateAnHttpClientWithBasicAuth(String key, String user, String password) throws Exception {
		scenarioData.put(key,HttpClient.builder()
		    	.withEventLoopGroup(eventLoopGroup)
		    	.withMonitorEnabled()
		    	.withBasicAuth(user, password)
		    	.build());
	}
	
	@When("^I create an http client \"([^\"]*)\" with bearer auth \"([^\"]*)\"$")
	public void iCreateAnHttpClientWithBearerAuth(String key, String token) throws Exception {
		scenarioData.put(key,HttpClient.builder()
		    	.withEventLoopGroup(eventLoopGroup)
		    	.withMonitorEnabled()
		    	.withBearerAuth(token)
		    	.build());
	}
	
	@When("^I get \"([^\"]*)\" using client \"([^\"]*)\" in event loop \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetUsingClientInEventLoopGettingResponse(String path, String clientKey, String eventloopKey, String resultKey) throws Exception {
		HttpClient client = (HttpClient) scenarioData.get(clientKey);
		EventLoop eventloop = (EventLoop) scenarioData.get(eventloopKey);
		AtomicBoolean done = new AtomicBoolean();
		eventloop.execute(()->{
			Future<FullHttpResponse> response = client.request().withEndpoint(LOCAL_ENDPOINT)
					.get(path).fullResponse();
			scenarioData.put(resultKey, response);
			done.set(true);
		});
		Awaitility.await().until(done::get);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" using http client \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostWithBodyUsingGenericClientGettingResponse(String path, String body, String clientKey, String resultKey) throws Exception {
		HttpClient client = (HttpClient) scenarioData.get(clientKey);
		Future<FullHttpResponse> response = client.request()
	    	.withEndpoint(LOCAL_ENDPOINT)
	    	.post(path)
	    	.withBodyWriter(b->b.writeCharSequence(body, CharsetUtil.UTF_8))
	    	.fullResponse();
	    scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body stream \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostWithBodyStreamGettingResponse(String path, String streamKey, String resultKey) throws Exception {
		AtomicReference<ChunckedBodyRequest> ref = new AtomicReference<>();
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.post(path)
			.withChunkedBody(ref::set)
			.fullResponse();
		Awaitility.await().until(()->ref.get()!=null);
		scenarioData.put(resultKey, response);
		scenarioData.put(streamKey, ref.get());
	}
	
	@When("^I send \"([^\"]*)\" to stream \"([^\"]*)\" getting result \"([^\"]*)\"$")
	public void iSendToStreamGettingResult(String content, String streamKey, String resultKey) throws Exception {
		ChunckedBodyRequest stream = (ChunckedBodyRequest) scenarioData.get(streamKey);
		scenarioData.put(resultKey, stream.send(content));
	}
	
	@When("I close request stream \"([^\\\"]*)\"")
	public void iCloseRequestStream(String streamKey) {
		ChunckedBodyRequest stream = (ChunckedBodyRequest) scenarioData.get(streamKey);
		stream.end();
	}
	
	@When("^I post \"([^\"]*)\" with body stream \"([^\"]*)\" handling objects \"([^\"]*)\" and getting response \"([^\"]*)\"$")
	public void iPostWithBodyStreamHandlingObjectsAndGettingResponse(String path, String streamKey, String objectsKey, String resultKey) throws Exception {
		AtomicReference<ChunckedBodyRequest> ref = new AtomicReference<>();
		List<String> objects = new ArrayList<>();
		scenarioData.put(objectsKey, objects);
		Future<Void> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.post(path)
			.withChunkedBody(ref::set)
			.stream().forEach(data->objects.add(data.toString(CharsetUtil.UTF_8)));
		Awaitility.await().until(()->ref.get()!=null);
		scenarioData.put(resultKey, response);
		scenarioData.put(streamKey, ref.get());
	}
	
	@When("^I get \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithClientGettingResponse(String path, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" using client \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetUsingClientGettingResponse(String path, String clientKey, String resultKey) throws Exception {
		Future<FullHttpResponse> response = ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT)
				.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" getting json response \"([^\"]*)\"$")
	public void iGetWithClientGettingTransformedResponseToAny(String path, String resultKey) throws Exception {
		Future<Any> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path).fullResponse(this::toAny);
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" with basic auth \"([^\"]*)\" \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithBasicAuthGettingResponse(String path, String user, String pass, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path)
			.withBasicAuth(user, pass)
			.fullResponse();
		scenarioData.put(resultKey, response);
	}

	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and basic auth \"([^\"]*)\" \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostWithBasicAuthGettingResponse(String path, String body, String user, String pass, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.post(path)
			.withBodyWriter(b->b.writeCharSequence(body, CharsetUtil.UTF_8))
			.withBasicAuth(user, pass)
			.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" with bearer auth \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithBearerAuthGettingResponse(String path, String token, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path)
			.withBearerAuth(token)
			.fullResponse();
		scenarioData.put(resultKey, response);
	}

	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and bearer auth \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostWithBearerAuthGettingResponse(String path, String body, String token, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.post(path)
			.withBodyWriter(b->b.writeCharSequence(body, CharsetUtil.UTF_8))
			.withBearerAuth(token)
			.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" throwing processing error getting response \"([^\"]*)\"$")
	public void iGetThrowingProcessingErrorGettingResponse(String path, String resultKey) throws Exception {
		Future<Any> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.get(path).fullResponse(r->{throw new RuntimeException("Processing error");});
			scenarioData.put(resultKey, response);
	}

	
	@Then("^I check that json \"([^\"]*)\" has property \"([^\"]*)\" equals to \"([^\"]*)\"$")
	public void iCheckThatAnyHasPropertyEqualsTo(String key, String property, String expected) throws Exception {
		@SuppressWarnings("unchecked")
		Future<Any> response = (Future<Any>) scenarioData.get(key);
		Any any = response.getNow();
		String[] propPath = property.split("\\.");
		assertThat(any.get((Object[])propPath).toString(), equalTo(expected));
	}
	
	@Then("^I check that json object (\\d+) in list \"([^\"]*)\" has property \"([^\"]*)\" equals to \"([^\"]*)\"$")
	public void iCheckThatJsonObjectInListHasPropertyEqualsTo(int index, String key, String property, String expected) throws Exception {
		List<?> objects = (List<?>) scenarioData.get(key);
		Any any = (Any) objects.get(index);
		String[] propPath = property.split("\\.");
		assertThat(any.get((Object[])propPath).toString(), equalTo(expected));
	}
	
	@Then("^I check that object (\\d+) in list \"([^\"]*)\" is equals to \"([^\"]*)\"$")
	public void iCheckThatObjectInListIsEqualsTo(int index, String key, String expected) throws Exception {
		List<?> objects = (List<?>) scenarioData.get(key);
		String value = (String) objects.get(index);
		assertThat(value, equalTo(expected));
	}
	
	private Any toAny(FullHttpResponse response) {
		byte[] data = new byte[response.content().readableBytes()];
		response.content().readBytes(data);
		return JsonIterator.deserialize(data);
	}
	
	@When("^I get \"([^\"]*)\" ignoring status getting response \"([^\"]*)\"$")
	public void iGetWithClientIgnoringStatusGettingResponse(String path, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.withIgnoreStatusCode()
			.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I delete \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iDeleteWithClientGettingResponse(String path, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.delete(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@Then("^I check that http client \"([^\"]*)\" has (\\d+) iddle connection$")
	public void iCheckThatHttpClientHasIddleConnection(String clientKey, int number) throws Exception {
		HttpClient client = (HttpClient) scenarioData.get(clientKey);
		assertThat(client.monitor().idleConnections(),equalTo(number));
	}
	
	@Then("^I check that client \"([^\"]*)\" has (\\d+) iddle connection$")
	public void iCheckThatClientHasIddleConnection(String clientKey, int number) throws Exception {
		Client<?> client = (Client<?>) scenarioData.get(clientKey);
		assertThat(client.monitor().idleConnections(),equalTo(number));
	}
	
	@Then("^I check that client \"([^\"]*)\" has (\\d+) active connection$")
	public void iCheckThatClientHasActiveConnection(String clientKey, int number) throws Exception {
		Client<?> client = (Client<?>) scenarioData.get(clientKey);
		Awaitility.await()
			.pollDelay(50, TimeUnit.MILLISECONDS)
			.until(client.monitor()::activeConnections,equalTo(number));
	}

	@Then("^I check that http client \"([^\"]*)\" has (\\d+) total connection$")
	public void iCheckThatHttpClientHasTotalConnection(String clientKey, int number) throws Exception {
		HttpClient client = (HttpClient) scenarioData.get(clientKey);
		assertThat(client.monitor().totalConnections(),equalTo(number));
	}
	
	@When("^I check that http client \"([^\"]*)\" has (\\d+) active connection$")
	public void iCheckThatHttpClientHasActiveConnection(String clientKey, int number) throws Exception {
		HttpClient client = (HttpClient) scenarioData.get(clientKey);
		Awaitility.await()
			.pollDelay(50, TimeUnit.MILLISECONDS)
			.until(client.monitor()::activeConnections,equalTo(number));
	}
	
	@Then("^I check that http client has (\\d+) iddle connection$")
	public void iCheckThatHttpClientHasIddleConnection(int number) throws Exception {
		assertThat(sutClient.monitor().idleConnections(),equalTo(number));
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
		Future<FullHttpResponse> response = sutClient
				.request().withEndpoint(new Endpoint(HttpEndpoint.HTTP_SCHEMA, new Address(LOCAL_ENDPOINT.address().host(), port)))
				.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" to port (\\d+) with basic auth \"([^\"]*)\" \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetToPortWithBasicAuthGettingResponse(String path, int port, String user, String pass, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient
				.request().withEndpoint(new Endpoint(HttpEndpoint.HTTP_SCHEMA, new Address(LOCAL_ENDPOINT.address().host(), port)))
				.withBasicAuth(user, pass)
				.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithClientGetingResponse(String path,String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.post(path)
			.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
			.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body supplier \"([^\"]*)\" getting json \"([^\"]*)\"$")
	public void iPostWithBodySupplierGettingJson(String path, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.post(path)
			.withBodySupplier(alloc->Unpooled.wrappedBuffer(body.getBytes()))
			.fullResponse();
		scenarioData.put(resultKey, response);
		
		
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" ignoring status getting response \"([^\"]*)\"$")
	public void iPostWithBodyIgnoringStatusGettingResponse(String path, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.post(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.withIgnoreStatusCode()
				.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I put \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPutWithBodyGettingResponse(String path, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.put(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I patch \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPatchWithBodyGettingResponse(String path, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.patch(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I options \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iOptionsWithBodyGettingResponse(String path, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.options(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" using client \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostUsingClientWithBodyGettingResponse(String path, String clientKey, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT)
				.post(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.fullResponse();
			scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and header \"([^\"]*)\" \"([^\"]*)\" getting json response \"([^\"]*)\"$")
	public void iPostWithBodyAndHeaderGettingResponse(String path, String body, String headerName, String headerValue, String resultKey) throws Exception {
		Future<Any> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.post(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.withHeader(headerName, headerValue)
				.fullResponse(this::toAny);
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and query param \"([^\"]*)\" \"([^\"]*)\" getting json response \"([^\"]*)\"$")
	public void iPostWithQueryParamGettingJsonResponse(String path, String body, String param, String value, String resultKey) throws Exception {
		Future<Any> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.post(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.param(param, value)
				.fullResponse(this::toAny);
		scenarioData.put(resultKey, response);
	}
	
	@When("^I send a full post request \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iSendAFullPostRequestWithBodyGettingResponse(String path, String body, String resultKey) throws Exception {
		FullHttpRequest fullRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path, Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.send(fullRequest)
				.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and response time (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyAndResponseTimeGettingResponse(String path, String body, int timeout, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.withResponseTimeout(timeout)
				.post(path)
				.withBodyWriter(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
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
	
	@Then("^I check that http response \"([^\"]*)\" has body$")
	public void iCheckThatHttpResponseHasBody2(String key, String body) throws Exception {
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
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(endpoint).get(endpoint.path()).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" with query params getting response \"([^\"]*)\"$")
	public void iGetWithQueryParamsGettingResponse(String path, String resultKey, Map<String,String> params) throws Exception {
		Future<FullHttpResponse> response = sutClient
				.request().withEndpoint(LOCAL_ENDPOINT)
				.get(path)
				.params(params)
				.fullResponse();
		scenarioData.put(resultKey, response);
	}

	@Given("^\"([^\"]*)\" proxy \"([^\"]*)\" as \"([^\"]*)\"$")
	public void socksProxyAs(ProxyType type, String host, String key) throws Exception {
		String[] hostPort = host.split(":");
	    Proxy proxy = new Proxy(hostPort[0], Integer.parseInt(hostPort[1]), type);
	    scenarioData.put(key, proxy);
	}
	
	@When("^I get url \"([^\"]*)\" through proxy \"([^\"]*)\" with username \"([^\"]*)\" and password \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetUrlThroughProxyWithUsernameAndPasswordGettingResponse(String endpointUrl, String proxyKey, String username, String password, String resultKey) throws Exception {
		Proxy proxy = (Proxy) scenarioData.get(proxyKey);
		HttpEndpoint target = HttpEndpoint.of(endpointUrl);
		ProxiedEndpoint endpoint = proxy(proxy,target,username,password);
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(endpoint).get(target.path()).fullResponse();
		scenarioData.put(resultKey, response);
	}

	
	@When("^I get url \"([^\"]*)\" through proxy \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostUrlWithDataThroughProxyGettingResponse(String endpointUrl, String proxyKey, String resultKey) throws Exception {
		Proxy proxy = (Proxy) scenarioData.get(proxyKey);
		HttpEndpoint target = HttpEndpoint.of(endpointUrl);
		ProxiedEndpoint endpoint = proxy(proxy,target,null,null);
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(endpoint).get(target.path()).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	private ProxiedEndpoint proxy(Proxy proxy,Endpoint target, String username, String password) {
		ProxiedEndpointBuilder builder = ProxiedEndpoint.of(target);
		if(username!=null && password!=null){
			builder.withUsername(username).withPassword(password);
		}
		switch(proxy.type()) {
		case SOCKS5:
			return builder.throughSocks5(proxy.address().host(), proxy.address().port());
		case SOCKS4:
			return builder.throughSocks4(proxy.address().host(), proxy.address().port());
		case HTTP:
		default:
			return builder.throughHTTP(proxy.address().host(), proxy.address().port());
		}
	}

	@Then("^I check that http objects \"([^\"]*)\" contains (\\d+) items$")
	public void iCheckThatHttpObjectsContainsItems(String key, int number) throws Exception {
		@SuppressWarnings("unchecked")
		List<HttpObject> objects = (List<HttpObject>) scenarioData.get(key);
		assertThat(objects,hasSize(number));
	}
	
	
//	@When("^I get url \"([^\"]*)\" getting sse stream \"([^\"]*)\"$")
//	public void iGetUrlGettingSSEStream(String endpointUrl, String resultKey) throws Exception {
//		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
//		List<ServerEvent> stream = new ArrayList<>();
//		sutClient.request().withEndpoint(endpoint).get(endpoint.path())
//				.sse().onEvent(event->stream.add(event)).sync();
//		scenarioData.put(resultKey, stream);
//	}
	
	@SuppressWarnings("unchecked")
	@Then("^I check that stream \"([^\"]*)\" contains (\\d+) items$")
	public void iCheckThatStreamContainsItems(String key, int number) throws Exception {
		Awaitility.await().until(()->(List<ByteBuf>) scenarioData.get(key),hasSize(number));
		List<ByteBuf> objects = (List<ByteBuf>) scenarioData.get(key);
		assertThat(objects,hasSize(number));
	}
	
	@Then("^I check that item (\\d+) of stream \"([^\"]*)\" is equal to$")
	public void iCheckThatItemOfStreamIsEqualTo(int item, String key, String expected) throws Exception {
		@SuppressWarnings("unchecked")
		List<ByteBuf> objects = (List<ByteBuf>) scenarioData.get(key);
		ByteBuf data = objects.get(item);
		assertThat(data.toString(CharsetUtil.UTF_8),equalTo(expected));
	}
	
	@Then("^I check that item (\\d+) of stream \"([^\"]*)\" is equal to \"([^\"]*)\"$")
	public void iCheckThatItemOfStreamIsEqualTo2(int item, String key, String expected) throws Exception {
		@SuppressWarnings("unchecked")
		List<ByteBuf> objects = (List<ByteBuf>) scenarioData.get(key);
		ByteBuf data = objects.get(item);
		assertThat(data.toString(CharsetUtil.UTF_8),equalTo(expected));
	}
	
	@When("^I connect to websocket \"([^\"]*)\" with uri \"([^\"]*)\" getting text stream \"([^\"]*)\"$")
	public void iConnectToWebsocketWithUriGettingTextStream(String wsKey, String uri, String stream) throws Exception {
		StringBuilder data = new StringBuilder();
		scenarioData.put(stream, data);
		WebsocketClient ws = sutClient.request()
				.withEndpoint(LOCAL_ENDPOINT)
				.websocket(uri)
				.onMessage(buff->data.append(buff.toString(CharsetUtil.UTF_8)));
		scenarioData.put(wsKey, ws);
	}
	
	@When("^I connect to websocket \"([^\"]*)\" with uri \"([^\"]*)\" getting text stream \"([^\"]*)\" and close future \"([^\"]*)\"$")
	public void iConnectToWebsocketWithUriGettingTextStreamAndCloseFuture(String wsKey, String uri, String stream, String closeKey) throws Exception {
		StringBuilder data = new StringBuilder();
		scenarioData.put(stream, data);
		WebsocketClient ws = sutClient.request()
				.withEndpoint(LOCAL_ENDPOINT)
				.websocket(uri)
				.onMessage(buff->data.append(buff.toString(CharsetUtil.UTF_8)));
		scenarioData.put(closeKey, ws.closeFuture());
		scenarioData.put(wsKey, ws);
	}
	
	@When("^I connect to websocket \"([^\"]*)\" with uri \"([^\"]*)\" getting clonnection future \"([^\"]*)\"$")
	public void iConnectToWebsocketWithUriGettingClonnectionFuture(String wsKey, String uri, String connectKey) throws Exception {
		WebsocketClient ws = sutClient.request()
				.withEndpoint(LOCAL_ENDPOINT)
				.websocket(uri);
		scenarioData.put(connectKey, ws.connectFuture());
		scenarioData.put(wsKey, ws);
	}
	
	@When("^I connect to websocket \"([^\"]*)\" with uri \"([^\"]*)\" getting text objects stream \"([^\"]*)\"$")
	public void iConnectToWebsocketWithUriGettingTextObjectsStream(String wsKey, String uri, String stream) throws Exception {
		List<String> objects = new ArrayList<>();
		scenarioData.put(stream, objects);
		WebsocketClient ws = sutClient.request()
				.withEndpoint(LOCAL_ENDPOINT)
				.websocket(uri)
				.onMessage(buff->objects.add(buff.toString(CharsetUtil.UTF_8)));
		scenarioData.put(wsKey, ws);
	}
	
	@Then("^I check that text stream \"([^\"]*)\" is equals to \"([^\"]*)\"$")
	public void iCheckThatTextIsEqualsTo(String key, String expected) throws Exception {
		StringBuilder data = (StringBuilder) scenarioData.get(key);
		Awaitility.await().until(()->((StringBuilder) scenarioData.get(key)).toString(),equalTo(expected));
		assertThat(data.toString(),equalTo(expected));
		data.delete(0, data.length());
	}
	
	@Then("^I check that text stream \"([^\"]*)\" content match with \"([^\"]*)\"$")
	public void iCheckThatTextStreamContentMatchWith(String key, String regex) throws Exception {
		StringBuilder data = (StringBuilder) scenarioData.get(key);
		Awaitility.await().until(()->((StringBuilder) scenarioData.get(key)).toString().matches(regex));
		assertTrue(data.toString().matches(regex));
		data.delete(0, data.length());
	}
	
	@When("^I send message \"([^\"]*)\" to websocket \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendMessageToWebsocketGetting(String msg, String wsKey, String result) throws Exception {
		WebsocketClient ws = (WebsocketClient) scenarioData.get(wsKey);
		scenarioData.put(result, ws.send(msg));
	}
	
	@When("^I close client connections \"([^\"]*)\"$")
	public void iCloseClientConnections(String key) throws Exception {
		scenarioData.put(key,sutClient.close());
	}
	
	@When("^I crate http client filter \"([^\"]*)\" of class \"([^\"]*)\"$")
	public void iCrateHttpClientFilterOfClass(String key, Class<?> clazz) throws Exception {
		scenarioData.put(key, clazz.newInstance());
	}
	
//	@When("^I get \"([^\"]*)\" with filter \"([^\"]*)\" getting response \"([^\"]*)\"$")
//	public void iGetWithFilterGettingResponse(String path, String filterKey, String resultKey) throws Exception {
//		Future<FullHttpResponse> response = sutClient.request()
//				.withEndpoint(LOCAL_ENDPOINT)
//				.withFilter((HttpRequestFilter) scenarioData.get(filterKey))
//				.get(path).fullResponse();
//		scenarioData.put(resultKey, response);
//	}
	
	@Given("^an ssl endpoint \"([^\"]*)\" for \"([^\"]*)\" using certificate \"([^\"]*)\" and private key from key pair \"([^\"]*)\"$")
	public void anSslEndpointForUsingCertificateAndPrivateKeyFromKeyPair(String resultKey, String url, String certKey, String keyPairKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(url);
		KeyPair key = (KeyPair) scenarioData.get(keyPairKey);
		X509Certificate cert = (X509Certificate) scenarioData.get(certKey);
		SSLEndpoint sslEndpoint = SSLEndpoint.builder()
			.address(endpoint.address())
			.schema(endpoint.schema())
			.key(key.getPrivate())
			.certs(Collections.singletonList(cert))
			.build();
		scenarioData.put(resultKey, sslEndpoint);
	}
	
	@When("^I get \"([^\"]*)\" form endpoint \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetFormEndpointGettingResponse(String path, String endpointKey, String resultKey) throws Exception {
		Endpoint endpoint = (Endpoint) scenarioData.get(endpointKey);
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(endpoint).get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I execute (\\d+) serialized get \"([^\"]*)\" getting response error ratio \"([^\"]*)\"$")
	public void iExecuteSerializedGetGettingResponseErrorRatio(int count, String path, String errorKey) throws Exception {
		int errors = 0;
		for(int i=0; i<count;i++) {
			io.netty.util.concurrent.Future<FullHttpResponse> response = sutClient.request()
					.withEndpoint(LOCAL_ENDPOINT)
					.get(path)
					.fullResponse()
					.await();
			if(!response.isSuccess()) {
				errors++;
			} else {
				response.getNow().release();
			}
		}
		scenarioData.put(errorKey, (double)errors/count);
	}
	
	@Then("^I execute (\\d+) parallel get \"([^\"]*)\" getting response error ratio \"([^\"]*)\"$")
	public void iExecuteParallelGetTimesGettingResponseErrorRatio(int count, String path, String errorKey) throws Exception {
		AtomicInteger errors = new AtomicInteger(0);
		PromiseCombiner combiner = new PromiseCombiner(ImmediateEventExecutor.INSTANCE);
		EventLoop loop = eventLoopGroup.next();
		for(int i=0; i<count;i++) {
			Future<FullHttpResponse> response = sutClient.request()
					.withEndpoint(LOCAL_ENDPOINT)
					.get(path)
					.fullResponse();
			Promise<Void> promise = loop.newPromise();
			combiner.add((io.netty.util.concurrent.Future<?>) promise);
			response.addListener(f->{
				if(response.isSuccess()) {
					response.getNow().release();
					promise.setSuccess(null);
				} else {
					errors.incrementAndGet();
					promise.setFailure(response.cause());
				}
			});
			
		}
		Promise<Void> agg = loop.newPromise();
		combiner.finish(agg);
		agg.await();
		scenarioData.put(errorKey, (double)errors.get()/count);
	}
	

	@When("^I get \"([^\"]*)\" handling json objects \"([^\"]*)\" and getting result \"([^\"]*)\"$")
	public void iGetHandlingJsonObjectsAndGettingResult(String path, String objectsKey, String resultKey) throws Exception {
		List<Any> objects = new ArrayList<>();
		scenarioData.put(objectsKey, objects);
		Future<Void> result = sutClient.request()
			.withEndpoint(LOCAL_ENDPOINT)
			.get(path)
			.stream()
				.<Any>withInitializer(ch->ch
						.addLast(new JsonObjectDecoder())
						.addLast(new JsonToAnyDecode()))
				.forEach(data->objects.add(data));
		scenarioData.put(resultKey, result);
	}
	
	@When("^I get \"([^\"]*)\" handling objects \"([^\"]*)\" and getting result \"([^\"]*)\"$")
	public void iGetHandlingObjectsAndGettingResult(String path, String objectsKey, String resultKey) throws Exception {
		List<String> objects = new ArrayList<>();
		scenarioData.put(objectsKey, objects);
		Future<Void> result = sutClient.request()
			.withEndpoint(LOCAL_ENDPOINT)
			.get(path)
			.stream()
				.forEach(data->objects.add(data.toString(CharsetUtil.UTF_8)));
		scenarioData.put(resultKey, result);
	}
	
	@Then("^I check that object list \"([^\"]*)\" has size (\\d+)$")
	public void iCheckThatObjectListHasSize(String key, int expected) throws Exception {
		List<?> list = (List<?>) scenarioData.get(key);
		assertThat(list,hasSize(expected));
	}

}
