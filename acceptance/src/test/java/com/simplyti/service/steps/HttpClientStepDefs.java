package com.simplyti.service.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.awaitility.Awaitility;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.simplyti.service.client.tracer.SimpleRequestTracer;
import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.http.request.StreamedHttpRequest;
import com.simplyti.service.clients.http.sse.ServerEvent;
import com.simplyti.service.clients.http.ws.WebSocketClient;
import com.simplyti.service.clients.proxy.ProxiedEndpoint;
import com.simplyti.service.clients.proxy.ProxiedEndpointBuilder;
import com.simplyti.service.clients.proxy.Proxy;
import com.simplyti.service.clients.proxy.Proxy.ProxyType;
import com.simplyti.service.clients.trace.RequestTracer;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.vavr.control.Try;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
	
	
	@Before
	public void checkProxy() {
		HttpEndpoint target = HttpEndpoint.of("http://httpbin:80/status/200");
		ProxiedEndpoint endpoint = ProxiedEndpoint.of(target).through("127.0.0.1", 3128, Proxy.ProxyType.HTTP);
		Awaitility.await().until(()->{
			Try<FullHttpResponse> result = Try.of(()->client
					.request()
					.withEndpoint(endpoint)
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
	
	@Given("^a single thread event loop group \"([^\"]*)\"$")
	public void aSingleThreadEventLoopGroup(String key) throws Exception {
		scenarioData.put(key, eventLoopGroup.next());
	}
	
	@When("^I create an http client \"([^\"]*)\" with event loop group \"([^\"]*)\"$")
	public void iCreateAnHttpClientWithEventLoopGroup(String key, String eventloopKey) throws Exception {
		EventLoopGroup eventloop = (EventLoopGroup) scenarioData.get(eventloopKey);
		scenarioData.put(key,HttpClient.builder()
		.eventLoopGroup(eventloop)
		.withCheckStatusCode()
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
	
	@When("^I post \"([^\"]*)\" with body stream \"([^\"]*)\", content part \"([^\"]*)\", length of (\\d+) getting response objects \"([^\"]*)\"$")
	public void iPostWithBodyStreamAndLengthOfSgettingResponse(String path, String streamKey, String cotentPart, int length, String responseObjects) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,length);
		List<HttpObject> responseStream = new ArrayList<>();
		scenarioData.put(responseObjects, responseStream);
		StreamedHttpRequest stream = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.send(request)
			.forEach(obj->responseStream.add(ReferenceCountUtil.retain(obj)));
		stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(cotentPart.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(streamKey, stream);
	}
	
	@When("^I post \"([^\"]*)\" using client \"([^\"]*)\" with body stream \"([^\"]*)\", content part \"([^\"]*)\", length of (\\d+) getting response objects \"([^\"]*)\"$")
	public void iPostWithBodyStreamAndLengthOfSgettingResponse(String path,String clientKey, String streamKey, String cotentPart, int length, String responseObjects) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,length);
		List<HttpObject> responseStream = new ArrayList<>();
		scenarioData.put(responseObjects, responseStream);
		StreamedHttpRequest stream = ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT)
			.send(request)
			.forEach(obj->responseStream.add(ReferenceCountUtil.retain(obj)));
		stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(cotentPart.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(streamKey, stream);
	}
	
	@Then("^I check that response stream \"([^\"]*)\" contains body \"([^\"]*)\"$")
	public void iCheckThatResponseStreamContainsBody(String responseStreamKey, String expected) throws Exception {
		@SuppressWarnings("unchecked")
		List<HttpObject> responseStream = (List<HttpObject>) scenarioData.get(responseStreamKey);
		CompositeByteBuf buff = new CompositeByteBuf(PooledByteBufAllocator.DEFAULT, false, 100);
		responseStream.stream()
			.filter(obj->obj instanceof HttpContent)
			.map(HttpContent.class::cast)
			.forEach(obj->buff.addComponent(true, obj.content()));
		
		assertThat(buff.toString(CharsetUtil.UTF_8),equalTo(expected));
		buff.release();
		buff.toString();
	}
	
	@When("^I send \"([^\"]*)\" to stream \"([^\"]*)\" getting result \"([^\"]*)\"$")
	public void iSendToStreamGettingResult(String data, String streamKey, String resultKey) throws Exception {
		StreamedHttpRequest stream = (StreamedHttpRequest) scenarioData.get(streamKey);
		Future<Void> result = stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(data.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(resultKey, result);
	}
	
	@When("^I send last \"([^\"]*)\" to stream \"([^\"]*)\" getting result \"([^\"]*)\"$")
	public void iSendLastToStreamGettingResult(String data, String streamKey, String resultKey) throws Exception {
		StreamedHttpRequest stream = (StreamedHttpRequest) scenarioData.get(streamKey);
		Future<Void> result = stream.send(new DefaultLastHttpContent(Unpooled.wrappedBuffer(data.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(resultKey, result);
	}
	
	@When("^I get \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithClientGettingResponse(String path, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I get \"([^\"]*)\" getting transformed response to any \"([^\"]*)\"$")
	public void iGetWithClientGettingTransformedResponseToAny(String path, String resultKey) throws Exception {
		Future<Any> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path).fullResponse(this::toAny);
		scenarioData.put(resultKey, response);
	}
	
	@Then("^I check that any \"([^\"]*)\" has property \"([^\"]*)\" equals to \"([^\"]*)\"$")
	public void iCheckThatAnyHasPropertyEqualsTo(String key, String property, String expected) throws Exception {
		@SuppressWarnings("unchecked")
		Future<Any> response = (Future<Any>) scenarioData.get(key);
		Any any = response.getNow();
		assertThat(any.get(property).toString(), equalTo(expected));
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
		assertThat(client.monitor().iddleConnections(),equalTo(number));
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
			.body(alloc->alloc.buffer().writeBytes(Unpooled.copiedBuffer(body, CharsetUtil.UTF_8)))
			.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and response time (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyAndResponseTimeGettingResponse(String path, String body, int timeout, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
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
		case HTTP:
		default:
			return builder.throughHTTP(proxy.address().host(), proxy.address().port());
		}
	}

	@Then("^I get url \"([^\"]*)\" getting http objects \"([^\"]*)\"$")
	public void iGetUrlGettingHttpResponse(String endpointUrl, String resultKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		List<HttpObject> objets = new ArrayList<>();
		sutClient.request().withEndpoint(endpoint).get(endpoint.path())
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
		sutClient.request().withEndpoint(endpoint).get(endpoint.path())
				.stream(data->stream.add(Unpooled.copiedBuffer(data))).sync();
		scenarioData.put(resultKey, stream);
	}
	
	@When("^I get url \"([^\"]*)\" getting sse stream \"([^\"]*)\"$")
	public void iGetUrlGettingSSEStream(String endpointUrl, String resultKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		List<ServerEvent> stream = new ArrayList<>();
		sutClient.request().withEndpoint(endpoint).get(endpoint.path())
				.sse(event->stream.add(event)).sync();
		scenarioData.put(resultKey, stream);
	}
	
	@SuppressWarnings("unchecked")
	@Then("^I check that stream \"([^\"]*)\" contains (\\d+) items$")
	public void iCheckThatStreamContainsItems(String key, int number) throws Exception {
		Awaitility.await().until(()->(List<ByteBuf>) scenarioData.get(key),hasSize(number));
		List<ByteBuf> objects = (List<ByteBuf>) scenarioData.get(key);
		assertThat(objects,hasSize(number));
	}
	
	@Then("^I check that item (\\d+) of stream \"([^\"]*)\" is equals to$")
	public void iCheckThatItemOfStreamIsEqualsTo(int item, String key, String expected) throws Exception {
		@SuppressWarnings("unchecked")
		List<ByteBuf> objects = (List<ByteBuf>) scenarioData.get(key);
		ByteBuf data = objects.get(item);
		assertThat(data.toString(CharsetUtil.UTF_8),equalTo(expected));
	}
	
	@When("^I connect to websocket \"([^\"]*)\" getting text stream \"([^\"]*)\"$")
	public void iConnectToWebsocketGettingTextStream(String wsKey, String stream) throws Exception {
		StringBuilder data = new StringBuilder();
		scenarioData.put(stream, data);
		WebSocketClient ws = client.request().withEndpoint(LOCAL_ENDPOINT)
			.websocket("/",frame->data.append(((TextWebSocketFrame)frame).text()));
		scenarioData.put(wsKey, ws);
	}
	
	@When("^I connect to websocket \"([^\"]*)\" getting text stream \"([^\"]*)\" and write \"([^\"]*)\"$")
	public void iConnectToWebsocketGettingTextStreamAndWrite(String wsKey, String stream, String arg3) throws Exception {
		StringBuilder data = new StringBuilder();
		scenarioData.put(stream, data);
		WebSocketClient ws = client.request().withEndpoint(LOCAL_ENDPOINT)
			.websocket("/",frame->data.append(((TextWebSocketFrame)frame).text()));
		scenarioData.put(wsKey, ws);
	}
	
	@When("^I connect to websocket \"([^\"]*)\" with address \"([^\"]*)\" getting text stream \"([^\"]*)\"$")
	public void iConnectToWebsocketWithAddressGettingTextStream(String wsKey, String endpointUrl, String stream) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		StringBuilder data = new StringBuilder();
		scenarioData.put(stream, data);
		WebSocketClient ws = client.request().withEndpoint(endpoint)
			.websocket("/",frame->data.append(((TextWebSocketFrame)frame).text()));
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
		WebSocketClient ws = (WebSocketClient) scenarioData.get(wsKey);
		scenarioData.put(result, ws.send(msg));
	}
	
	
	@Given("^a simple client request tracer \"([^\"]*)\"$")
	public void aSimpleClientRequestTracer(String key) throws Exception {
	    this.scenarioData.put(key, new SimpleRequestTracer());
	}

	@When("^I get \"([^\"]*)\" getting response \"([^\"]*)\" with request tracer \"([^\"]*)\"$")
	public void iGetGettingResponseWithRequestTracer(String path, String resultKey, String tracerKey) throws Exception {
		RequestTracer<?,?> tracer =  (RequestTracer<?,?>) scenarioData.get(tracerKey);
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.withTracer(tracer)
				.get(path).fullResponse();
		
		scenarioData.put(resultKey, response);
	}

	@Then("^I check that tracer \"([^\"]*)\" contains (\\d+) request$")
	public void iCheckThatTracerContainsRequest(String tracerKey, int count) throws Exception {
		SimpleRequestTracer tracer =  (SimpleRequestTracer) scenarioData.get(tracerKey);
		assertThat(tracer.requests(),hasSize(1));
	}

	@When("^I close client connections \"([^\"]*)\"$")
	public void iCloseClientConnections(String key) throws Exception {
		scenarioData.put(key,sutClient.close());
	}
	
	
}
