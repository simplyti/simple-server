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
import com.simplyti.service.clients.Client;
import com.simplyti.service.clients.GenericClient;
import com.simplyti.service.clients.endpoint.Address;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.http.sse.ServerEvent;
import com.simplyti.service.clients.http.stream.request.StreamedInputHttpRequestBuilder;
import com.simplyti.service.clients.http.websocket.WebsocketClient;
import com.simplyti.service.clients.proxy.ProxiedEndpoint;
import com.simplyti.service.clients.proxy.ProxiedEndpointBuilder;
import com.simplyti.service.clients.proxy.Proxy;
import com.simplyti.service.clients.proxy.Proxy.ProxyType;
import com.simplyti.service.commons.netty.Promises;
import com.simplyti.util.concurrent.Future;
import com.simplyti.service.clients.channel.ClientChannel;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

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
	
	@When("^I create an generic client \"([^\"]*)\" with pool idle timeout (\\d+)$")
	public void iCreateAnGenericClientWithPoolIdleTimeout(String key, int iddleTime) throws Exception {
		scenarioData.put(key,Client.builder()
	    	.withEventLoopGroup(eventLoopGroup)
	    	.withMonitorEnabled()
	    	.withChannelPoolIdleTimeout(iddleTime)
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
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" using generic client \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostWithBodyUsingGenericClientGettingResponse(String path, String body, String clientKey, String resultKey) throws Exception {
	    GenericClient client = (GenericClient) scenarioData.get(clientKey);
	    FullHttpRequest fullRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path, Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
	    fullRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH,fullRequest.content().readableBytes());
	    Future<HttpResponse> response = client.request()
	    	.withEndpoint(LOCAL_ENDPOINT)
	    	.withChannelInitialize(ch->{
	    		ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
	    		ch.pipeline().addLast(new HttpClientCodec());
	    	})
	    	.channel()
	    	.thenCombine(ch-> {
	    		Promise<ClientChannel> promise = ch.eventLoop().newPromise();
	    		ChannelFuture future = ch.writeAndFlush(fullRequest);
	    		Promises.ifSuccessMap(future, promise, __->ch);
	    		return promise;
	    	})
	    	.thenCombine(ch->{
	    		Promise<HttpResponse> promise = ch.eventLoop().newPromise();
	    		ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpResponse>() {

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, HttpResponse msg) throws Exception {
						ctx.pipeline().remove(this);
						promise.setSuccess(msg);
						ch.release();
					}
				});
	    		return promise;
	    	});
	    scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body stream \"([^\"]*)\", content part \"([^\"]*)\", length of (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyStreamAndLengthOfSgettingResponse(String path, String streamKey, String cotentPart, int length, String responseKey) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,length);
		StreamedInputHttpRequestBuilder stream = sutClient.request().withEndpoint(LOCAL_ENDPOINT).send(request);
		scenarioData.put(streamKey, stream);
		Future<FullHttpResponse> response = stream.fullResponse();
		stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(cotentPart.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(responseKey, response);
	}
	
	@When("^I post \"([^\"]*)\" using client \"([^\"]*)\" with body stream \"([^\"]*)\", content part \"([^\"]*)\", length of (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyStreamAndLengthOfSgettingResponse(String path,String clientKey, String streamKey, String cotentPart, int length, String responseKey) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,length);
		StreamedInputHttpRequestBuilder stream = ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT).send(request);
		scenarioData.put(streamKey, stream);
		Future<FullHttpResponse> response = stream.fullResponse();
		stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(cotentPart.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(responseKey, response);
	}
	
	@When("^I post \"([^\"]*)\" using client \"([^\"]*)\" with body stream \"([^\"]*)\", content part \"([^\"]*)\" from loop group \"([^\"]*)\", length of (\\d+) getting response \"([^\"]*)\"$")
	public void iPostUsingClientWithBodyStreamContentPartFromLoopGroupLengthOfGettingResponse(String path, String clientKey, String streamKey, String cotentPart, String eventLoopKey, int length, String responseKey) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,length);
		StreamedInputHttpRequestBuilder stream = ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT).send(request);
		scenarioData.put(streamKey, stream);
		Future<FullHttpResponse> response = stream.fullResponse();
		((EventLoopGroup)scenarioData.get(eventLoopKey)).next().execute(()->
			stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(cotentPart.getBytes(CharsetUtil.UTF_8)))));
		scenarioData.put(responseKey, response);
	}
	
	@When("^I send \"([^\"]*)\" to stream \"([^\"]*)\" getting result \"([^\"]*)\"$")
	public void iSendToStreamGettingResult(String data, String streamKey, String resultKey) throws Exception {
		StreamedInputHttpRequestBuilder stream = (StreamedInputHttpRequestBuilder) scenarioData.get(streamKey);
		Future<Void> result = stream.send(new DefaultHttpContent(Unpooled.wrappedBuffer(data.getBytes(CharsetUtil.UTF_8))));
		scenarioData.put(resultKey, result);
	}

	@When("^I get \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iGetWithClientGettingResponse(String path, String resultKey) throws Exception {
		Future<FullHttpResponse> response = sutClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get(path).fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" using client \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostUsingClientGettingResponse(String path, String clientKey, String resultKey) throws Exception {
		Future<FullHttpResponse> response = ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT)
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
		assertThat(client.monitor().activeConnections(),equalTo(number));
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
			.withBody(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
			.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" using client \"([^\"]*)\" with body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iPostUsingClientWithBodyGettingResponse(String path, String clientKey, String body, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  ((HttpClient)scenarioData.get(clientKey)).request().withEndpoint(LOCAL_ENDPOINT)
				.post(path)
				.withBody(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.fullResponse();
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
	
	@When("^I send a post request \"([^\"]*)\" with content-lenght (\\d+) to stream \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iSendAPostRequestWithContentLenghtToStreamGettingResponse(String path, int length, String streamKey, String resultKey) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH,length);
		StreamedInputHttpRequestBuilder streamed = sutClient.request().withEndpoint(LOCAL_ENDPOINT).send(request);
		scenarioData.put(streamKey, streamed);
		Future<FullHttpResponse>  response = streamed.fullResponse();
		scenarioData.put(resultKey, response);
	}
	
	@When("^I send content \"([^\"]*)\" to http stream \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendContentToHttpStreamGetting(String content, String streamKey, String resultKey) throws Exception {
		StreamedInputHttpRequestBuilder streamed = (StreamedInputHttpRequestBuilder) scenarioData.get(streamKey);
		Future<Void> result = streamed.send(new DefaultHttpContent(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)));
		scenarioData.put(resultKey, result);
	}
	
	@When("^I send last content \"([^\"]*)\" to http stream \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendLastContentToHttpStreamGetting(String content, String streamKey, String resultKey) throws Exception {
		StreamedInputHttpRequestBuilder streamed = (StreamedInputHttpRequestBuilder) scenarioData.get(streamKey);
		Future<Void> result = streamed.send(new DefaultLastHttpContent(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)));
		scenarioData.put(resultKey, result);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and response time (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyAndResponseTimeGettingResponse(String path, String body, int timeout, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.withResponseTimeout(timeout)
				.post(path)
				.withBody(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
				.fullResponse();
			scenarioData.put(resultKey, response);
	}
	
	@When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and read timeout (\\d+) getting response \"([^\"]*)\"$")
	public void iPostWithBodyAndReadTimeGettingResponse(String path, String body, int timeout, String resultKey) throws Exception {
		Future<FullHttpResponse> response =  sutClient.request().withEndpoint(LOCAL_ENDPOINT)
				.withReadTimeout(timeout)
				.post(path)
				.withBody(buffer->buffer.writeCharSequence(body, CharsetUtil.UTF_8))
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
				.stream().forEach(objets::add).await();
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
		sutClient.request().withEndpoint(endpoint)
				.get(endpoint.path())
				.stream()
					.onData(data->stream.add(Unpooled.copiedBuffer(data))).sync();
		scenarioData.put(resultKey, stream);
	}
	
	@When("^I get url \"([^\"]*)\" getting sse stream \"([^\"]*)\"$")
	public void iGetUrlGettingSSEStream(String endpointUrl, String resultKey) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		List<ServerEvent> stream = new ArrayList<>();
		sutClient.request().withEndpoint(endpoint).get(endpoint.path())
				.sse().onEvent(event->stream.add(event)).sync();
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
		WebsocketClient ws = client.request().withEndpoint(LOCAL_ENDPOINT).websocket();
		ws.onData(buff->data.append(buff.toString(CharsetUtil.UTF_8)));
		scenarioData.put(wsKey, ws);
	}
	
	@When("^I connect to websocket \"([^\"]*)\" with address \"([^\"]*)\" getting text stream \"([^\"]*)\"$")
	public void iConnectToWebsocketWithAddressGettingTextStream(String wsKey, String endpointUrl, String stream) throws Exception {
		HttpEndpoint endpoint = HttpEndpoint.of(endpointUrl);
		StringBuilder data = new StringBuilder();
		scenarioData.put(stream, data);
		WebsocketClient ws = client.request().withEndpoint(endpoint).websocket();
		ws.onData(buff->data.append(buff.toString(CharsetUtil.UTF_8)));
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
	
}
