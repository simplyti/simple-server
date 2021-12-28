package com.simplyti.service.steps;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;

import com.google.inject.Module;
import com.simplyti.service.DefaultServer;
import com.simplyti.service.Server;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.builder.di.guice.GuiceService;
import com.simplyti.service.builder.di.guice.GuiceServiceBuilder;
import com.simplyti.service.client.SimpleHttpClient;
import com.simplyti.service.client.SimpleHttpResponse;
import com.simplyti.service.clients.endpoint.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.http.exception.HttpException;
import com.simplyti.service.examples.hook.TestServerStopHookModule;

import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ImmediateEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.StringUtil;
import io.vavr.control.Try;

import static io.vavr.control.Try.run;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

public class ServiceBuilderStepDefs {
	
	private static final Endpoint LOCAL_ENDPOINT = HttpEndpoint.of("http://localhost:8080");
	private static final DateTimeFormatter CACHE_DATE_PATTERN = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	private String tempDir = System.getProperty("user.dir")+"/target/tempDir";
	
	@Inject
	private  SimpleHttpClient client;
	
	@Inject
	@Named("singleton")
	private HttpClient http;
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	private List<Future<Server>> services;
	
	@Inject
	private EventLoopGroup eventLoopGroup;
	
	@ParameterType(".*")
    public Class<?> clazz(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	return Class.forName(className.replaceAll("^\"", "").replaceAll("\"$", ""));
    }
	
	@ParameterType(".*")
    public List<String> list(String list) {
    	return Arrays.asList(list.replaceAll("^\"", "").replaceAll("\"$", "").split("\\s*,\\s*"));
    }
	
	@When("I start a service {string} with API {clazz}")
	public void iStartAServiceWithAPI(String key, Class<?extends ApiProvider> api) throws Exception {
		Future<Server> futureService = GuiceService.builder()
			.withLog4J2Logger()
			.withApi(api)
			.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	

	@When("I start a service {string} with required client auth and API {clazz}")
	public void iStartAServiceWithRequiredClientAuthAndAPI(String key, Class<?extends ApiProvider> api) throws Exception {
		Future<Server> futureService = GuiceService.builder()
			.withLog4J2Logger()
			.withApi(api)
			.withSslClientAuth(ClientAuth.REQUIRE)
			.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}

	
	@When("I start a service {string} with max body size {int} with API {clazz}")
	public void iStartAServiceWithMaxBodySizeWithAPI(String key, int maxBodySize, Class<?extends ApiProvider> api) throws Exception {
		Future<Server> futureService = GuiceService.builder()
				.withLog4J2Logger()
				.withApi(api)
				.withMaxBodySize(maxBodySize)
				.build().start();
			services.add(futureService);
			scenarioData.put(key, futureService);
	}

	
	@When("^I start a service \"([^\"]*)\"$")
	public void iStartAService(String key) throws Exception {
		Future<Server> futureService = GuiceService.builder()
				.withLog4J2Logger()
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("I start a service {string} with module {clazz}")
	public void iStartAServiceWithModule(String key, Class<? extends Module> module) throws Exception {
		Future<Server> futureService = GuiceService.builder()
				.withLog4J2Logger()
				.withModule(module)
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@Given("^next file \"([^\"]*)\" with createdDate \"([^\"]*)\" and content$")
	public void nextFileWithCreateDateAndContent(String fileName, String datekey, String content) throws Exception {
	    Path path = Paths.get(fileName.replaceAll("#tempdir", tempDir));
	    path.getParent().toFile().mkdirs();
	    FileUtils.write(path.toFile(), content, CharsetUtil.UTF_8);
	    scenarioData.put(datekey, path.toFile().lastModified());
	}
	
	@Given("^next file \"([^\"]*)\" with content$")
	public void nextFileWithContent(String fileName, String content) throws Exception {
	    Path path = Paths.get(fileName.replaceAll("#tempdir", tempDir));
	    path.getParent().toFile().mkdirs();
	    FileUtils.write(path.toFile(), content, CharsetUtil.UTF_8);
	}
	
	@After
	public void cleanTempDir() throws IOException {
		FileUtils.deleteDirectory(Paths.get(tempDir).toFile());
	}
	
	@When("I send files {list} to {string} getting {string}")
	public void iSendFileGetting(List<String> fileNames, String path, String resultKey) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		HttpDataFactory factory = new DefaultHttpDataFactory(false);
		HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(factory,request, true);
		
		for(String fileName:fileNames){
			Path filePath = Paths.get(fileName.replaceAll("#tempdir", tempDir));
			encoder.addBodyFileUpload(filePath.getFileName().toString(), filePath.toFile(), "text/plain", false);
		}
		
		request = encoder.finalizeRequest();
		scenarioData.put(resultKey, client.send(request,encoder).get());
	}

	
	@When("^I start a service \"([^\"]*)\" with file serve \"([^\"]*)\" on \"([^\"]*)\"$")
	public void iStartAServiceWithFileServeOn(String key, String directory, String path) throws Exception {
		String thedir = directory.replaceAll("#tempdir", tempDir);
		Future<Server> futureService = GuiceService.builder()
				.withLog4J2Logger()
				.withFileServe(path,thedir)
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("I start a service {string} with file serve {string} on {string} and API {clazz}")
	public void iStartAServiceWithFileServeOnAndAPI(String key, String directory, String path, Class<?extends ApiProvider> api) throws Exception {
		String thedir = directory.replaceAll("#tempdir", tempDir);
		Future<Server> futureService = GuiceService.builder()
				.withLog4J2Logger()
				.withFileServe(path,thedir)
				.withApi(api)
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("^I stop server \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iStopServerGetting(String key, String resultKey) throws Exception {
		@SuppressWarnings("unchecked")
		Future<DefaultServer> futureService = (Future<DefaultServer>) scenarioData.get(key);
		scenarioData.put(resultKey,futureService.getNow().stop());
	}
	
	
	@When("^I try to start a service \"([^\"]*)\" with options getting error \"([^\"]*)\":$")
	public void iTryToStartAServiceWithOptionsGettingError(String key, String errorKey, List<Map<String, String>> options) throws Exception {
		try{
			iStartAServiceWithOptions(key,options);
		}catch(Throwable error) {
			scenarioData.put(errorKey, error);
		}
	}
	
	@Given("^a server stop hook module \"([^\"]*)\"$")
	public void aServerStopHookModule(String key) throws Exception {
		scenarioData.put(key, new TestServerStopHookModule());
	}
	
	@Then("^I check that stop hook in \"([^\"]*)\" was invoked$")
	public void iCheckThatStopHookInWasInvoked(String key) throws Exception {
		TestServerStopHookModule  module = (TestServerStopHookModule) scenarioData.get(key);
		assertThat(module.wasInvoked(), equalTo(true));
	}
	
	@SuppressWarnings("unchecked")
	@When("^I start a service \"([^\"]*)\" with options:$")
	public void iStartAServiceWithOptions(String key, List<Map<String, String>> options) {
		GuiceServiceBuilder builder = GuiceService.builder();
		options.forEach(option->{
			if(option.get("option").equals("withLog4J2Logger")) {
				builder.withLog4J2Logger();
			} else if(option.get("option").equals("withApi")) {
				builder.withApi((Class<? extends ApiProvider>)Try.of(()->Class.forName(option.get("value"))).get());
			} else if(option.get("option").equals("verbose")) {
				builder.verbose();
			} else if(option.get("option").equals("withEventLoopGroup") && option.get("value").equals("#managed")) {
				builder.withEventLoopGroup(eventLoopGroup);
			} else if(option.get("option").equals("listener")) {
				if(option.get("value").startsWith("ssl:")) {
					builder.withListener()
					.port(Integer.parseInt(option.get("value").substring(4)))
					.secured()
					.end();
				} else {
					builder.withListener()
					.port(Integer.parseInt(option.get("value")))
					.end();
				}
				
			} else if(option.get("option").equals("withModule")) {
				String value = option.get("value");
				if(value.startsWith("#")) {
					builder.withModule((Module)scenarioData.get(value));
				} else if(value.matches("^[^\\(]+\\(.*\\)$")){
					Matcher matcher = Pattern.compile("^([^\\(]+)\\((.*)\\)$").matcher(value);
					matcher.matches();
					Class<? extends Module> clazz = (Class<? extends Module>) Try.of(()->Class.forName(matcher.group(1))).get();
					String[] args = matcher.group(2).split(",");
					builder.withModule(construct(clazz,args));
				} else {
					builder.withModule((Class<? extends Module>)Try.of(()->Class.forName(value)).get());
				}
			} else {
				throw new IllegalArgumentException("Unknown option "+option);
			}
		});
		Future<Server> futureService = builder.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}

	private Module construct(Class<? extends Module> clazz, String[] args) {
		@SuppressWarnings("unchecked")
		Constructor<?extends Module>[] constructors = (Constructor<? extends Module>[]) clazz.getConstructors();
		Object[] constructorArgs = buildArgs(constructors[0].getParameterTypes(),args);
		if(constructors.length==1) {
			return Try.of(()->constructors[0].newInstance(constructorArgs)).get();
		}else {
			List<Constructor<? extends Module>> matchArgConstructor = Stream.of(constructors).filter(constructor->constructor.getParameterTypes().length==constructorArgs.length)
				.collect(Collectors.toList());
			if(matchArgConstructor.size()==1) {
				return Try.of(()->matchArgConstructor.get(0).newInstance(constructorArgs)).get();
			}else {
				Class<?>[] types = Stream.of(constructorArgs).map(object->object.getClass()).toArray(Class<?>[]::new);
				Optional<Constructor<? extends Module>> constructor = matchArgConstructor.stream().filter(it->matchParameters(it.getParameterTypes(),types)).findFirst();
				if(constructor.isPresent()) {
					return Try.of(()->constructor.get().newInstance(constructorArgs)).get();
				}else {
					return null;
				}
			}
		}
	}


	private boolean matchParameters(Class<?>[] parameterTypes, Class<?>[] types) {
		for(int i=0;i<types.length;i++) {
			if(!parameterTypes[i].equals(types[i])) {
				return false;
			}
		}
		return true;
	}

	private Object[] buildArgs(Class<?>[] parameterTypes, String[] args) {
		return Stream.of(args).map(value->{
			if(value.startsWith("#")) {
				return scenarioData.get(value);
			}else {
				return value;
			}
		}).toArray();
	}

	@After
	public void stop() throws InterruptedException  {
		services.stream()
			.forEach(futureService->Awaitility.await().until(futureService::isDone));
		
		services.stream().filter(Future::isSuccess)
			.map(Future::getNow)
			.forEach(service->run(()->service.stop().await()));
	}
	
	@When("^I try to send a \"([^\\s]*) ([^\"]*)\" getting \"([^\"]*)\"$")
	public void iTryToSendAGetting(String method, String path, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,null,null).await());
	}
	
	@Then("^I send (\\d+) serialized request \"([^\\s]*) ([^\"]*)\" getting response error ratio \"([^\"]*)\"$")
	public void iSendSerializedRequestGettingResponseErrorRatio(int count, String method, String path, String errorKey) throws Exception {
		int errors = 0;
		for(int i=0; i<count;i++) {
			Future<SimpleHttpResponse> response = send(null,method,path,null,null).await();
			if(!response.isSuccess()) {
				errors++;
			} else if(response.getNow().status()!=200) {
				errors++;
			}
		}
		scenarioData.put(errorKey, (double)errors/count);
	}
	
	@Then("I send {int} serialized get request to {string} getting response error ratio {string}")
	public void iSendSerializedGetRequestToGettingResponseErrorRatio(int count, String path, String errorKey) throws InterruptedException {
		int errors = 0;
		for(int i=0; i<count;i++) {
			Future<FullHttpResponse> response = http.request()
					.withEndpoint(LOCAL_ENDPOINT)
					.get(path)
					.fullResponse().await();
			if(!response.isSuccess()) {
				errors++;
			} else {
				response.getNow().release();
				if(response.getNow().status().code()!=200) {
					errors++;
				}
			}
		}
		scenarioData.put(errorKey, (double)errors/count);
	}
	
	@Then("^I send (\\d+) parallel request \"([^\\s]*) ([^\"]*)\" with body \"([^\"]*)\" getting response error ratio \"([^\"]*)\"$")
	public void iSendParallelRequestWithBodyGettingResponseErrorRatio(int count, String method, String path, String body, String errorKey) throws Exception {
		AtomicInteger errors = new AtomicInteger(0);
		PromiseCombiner combiner = new PromiseCombiner(ImmediateEventExecutor.INSTANCE);
		EventLoop loop = eventLoopGroup.next();
		for(int i=0; i<count;i++) {
			Future<SimpleHttpResponse> response = send(null,method,path,body,null);
			Promise<Void> promise = loop.newPromise();
			combiner.add((Future<?>) promise);
			response.addListener(f->{
				if(response.isSuccess()) {
					if(response.getNow().status()!=200) {
						errors.incrementAndGet();
					}
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
	
	@Then("^I send (\\d+) parallel request \"([^\\s]*) ([^\"]*)\" getting response error ratio \"([^\"]*)\"$")
	public void iSendParallelRequestGettingResponseErrorRatio(int count, String method, String path, String errorKey) throws Exception {
		iSendParallelRequestWithBodyGettingResponseErrorRatio(count, method, path, null, errorKey);
	}
	
	@Then("^I check that error ratio \"([^\"]*)\" is (\\d+\\.\\d+)$")
	public void iCheckThatErrorRatioIs(String resultKey, double expect) throws Exception {
		double errors = (double) scenarioData.get(resultKey);
		assertThat(errors,equalTo(expect));
	}
	

	@Then("^I check that error ratio \"([^\"]*)\" less than (\\d+\\.\\d+)$")
	public void iCheckThatErrorRatioLessThan(String resultKey, double expect) throws Exception {
		double errors = (double) scenarioData.get(resultKey);
		assertThat(errors,lessThan(expect));
	}

	@When("^I send an HTTP \"([^\"]*)\" \"([^\\s]*) ([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAHttpVersionGetting(String version, String method, String path, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(HttpVersion.valueOf("HTTP/"+version),method,path,null,null).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" following auth redirect of \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAFollowingAuthRedirectOfGetting(String method, String path, String responseKey, String newResponseKey) throws Exception {
		FullHttpResponse response = (FullHttpResponse) ((Future<?>) scenarioData.get(responseKey)).get();
		QueryStringDecoder decoder = new QueryStringDecoder(response.headers().get(HttpHeaderNames.LOCATION));
		QueryStringEncoder encoder = new QueryStringEncoder(path);
		encoder.addParam("state", decoder.parameters().get("state").get(0));
		encoder.addParam("code", "XXXXXX");
		scenarioData.put(newResponseKey, send(null,method, encoder.toString(),null,null).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" with header \"([^\"]*)\" with value \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithHeaderWithValueGetting(String method, String path, String header, String value,String resultKey) throws Exception {
		HttpHeaders headers = new DefaultHttpHeaders().add(header, value);
		scenarioData.put(resultKey, send(null,method,path,null,headers).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" with cookies from response \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithCookiesFromResponseGetting(String method, String path, String responseKey, String resultKey) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(responseKey);
		
		List<String> cookies = response.headers().getAll(HttpHeaderNames.SET_COOKIE).stream()
				.map(ClientCookieDecoder.LAX::decode)
				.map(ServerCookieEncoder.LAX::encode)
				.collect(Collectors.toList());
		
		HttpHeaders headers = new DefaultHttpHeaders().add(HttpHeaderNames.COOKIE, cookies);
		scenarioData.put(resultKey, send(null,method,path,null,headers).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" with (\\d+) bytes random body getting \"([^\"]*)\"$")
	public void iSendAWithBytesRandomBodyGetting(String method, String path, int size, String resultKey) throws Exception {
		String body = random(size);
		scenarioData.put(resultKey, send(null,method,path,body,null).get());
	}
	
	@When("^I post \"([^\"]*)\" with (\\d+) bytes random body an continue expected getting \"([^\"]*)\"$")
	public void iSendAWithBytesRandomBodyAnConueExpectedGetting(String path, int size, String resultKey) throws Exception {
	    Future<FullHttpResponse> result = http.request().withEndpoint(LOCAL_ENDPOINT)
	    		.post(path)
		    	.withBodyWriter(b->b.writeCharSequence(random(size), CharsetUtil.UTF_8))
		    	.withHeader(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE)
		    	.fullResponse();
	    scenarioData.put(resultKey, result);
	}
	
	@Then("^I check that response \"([^\"]*)\" has body size (\\d+)$")
	public void iCheckThatResponseHasBodySize(String key, int size) throws Exception {
		@SuppressWarnings("unchecked")
		Future<FullHttpResponse> result = (Future<FullHttpResponse>) scenarioData.get(key);
		assertThat(result.getNow().content().readableBytes(), equalTo(size));
		result.getNow().release();
	}

	
	private String random(int targetStringLength) {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}

	@When("^I send a \"([^\\s]*) ([^\"]*)\" with body '([^']*)' getting \"([^\"]*)\"$")
	public void iSendAWithJsonBodyGetting(String method, String path, String body, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,body,null).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" using ssl port (\\d+) getting \"([^\"]*)\"$")
	public void iSendAUsingPortGetting(String method, String path, int port, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,port,true,null,null,null).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" using ssl port (\\d+) with sni \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAUsingSslPortWithSniGetting(String method, String path, int port, String sni, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,port,true,sni,null,null).get());
	}
	
	@When("^I asynchronously send a \"([^\\s]*) ([^\"]*)\" with body \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iAsynchronouslySendAGetting(String method, String path, String body, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,body,null));
	}
	
	public Future<SimpleHttpResponse> send(HttpVersion version,String method, String path, String body, HttpHeaders headers) {
		return send(version, method, path, 8080, false, null, body, headers);
	}

	private Future<SimpleHttpResponse> send(HttpVersion version,String method, String path, int port, boolean ssl,String sni, String body, HttpHeaders headers) {
		HttpMethod httpMethod = HttpMethod.valueOf(method);
		if(httpMethod.equals(HttpMethod.POST)) {
			return client.post(path,body);
		}else if(httpMethod.equals(HttpMethod.GET)) {
			return  client.get(version,path,port,ssl,sni,headers);
		}else if(httpMethod.equals(HttpMethod.DELETE)) {
			return  client.delete(path);
		}else {
			return client.method(new HttpMethod(method),path,body);
		}
	}

	@Then("^I check that \"([^\"]*)\" is equals to \"([^\"]*)\"$")
	public void iCheckThatIsEqualsTo(String key, String expected) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.body(),equalTo(expected));
	}
	
	@Then("^I check that \"([^\"]*)\" is equals to '([^']*)'$")
	public void iCheckThatIsEqualsToPojo(String key, String expected) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.body(),equalTo(expected));
	}
	
	@Then("^I check that \"([^\"]*)\" has status code (\\d+)$")
	public void iCheckThatHasStatusCode(String key, int code) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.status(),equalTo(code));
	}
	
	@Then("^I check that \"([^\"]*)\" is not complete$")
	public void iCheckThatIsNotComplete(String key) throws Exception {
		Thread.sleep(100);
		Future<?> future = (Future<?>) scenarioData.get(key);
		assertThat(future.isDone(),equalTo(false));
	}
	
	@Then("^I check that \"([^\"]*)\" is success$")
	public void iCheckThatIsSuccess(String key) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		Awaitility.await().until(futureService::isDone);
		if(!futureService.isSuccess()) {
			futureService.cause().printStackTrace();
		}
		assertThat(futureService.isSuccess(),equalTo(true));
	}
	
	@Then("^I check that \"([^\"]*)\" is finished$")
	public void iCheckThatIsFinished(String key) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		Awaitility.await().until(futureService::isDone);
	}

	
	@Then("^I check that \"([^\"]*)\" is failure$")
	public void iCheckThatIsFailure(String key) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		Awaitility.await().until(futureService::isDone);
		assertThat(futureService.isSuccess(),equalTo(false));
	}
	
	@Then("I check that error cause of {string} is instancence of {clazz}")
	public void iCheckThatErrorCauseOfIsInstancenceOf(String key, Class<? extends Throwable> clazz) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		assertThat(futureService.cause(),instanceOf(clazz));
	}
	
	@Then("^I check that error cause of \"([^\"]*)\" contains message \"([^\"]*)\"$")
	public void iCheckThatErrorCauseOfContainsMessage(String key, String message) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		assertThat(futureService.cause().getMessage(),equalTo(message));
	}

	@Then("^I check that error failure message of \"([^\"]*)\" is \"([^\"]*)\"$")
	public void iCheckThatErrorFailureMessageOfIs(String key, String expected) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		assertThat(futureService.isSuccess(),equalTo(false));
		assertThat(futureService.cause().getMessage(),equalTo(expected));
	}

	@Then("^I check that error failure message of \"([^\"]*)\" contains \"([^\"]*)\"$")
	public void iCheckThatErrorFailureMessageOfContains(String key, String expected) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		assertThat(futureService.isSuccess(),equalTo(false));
		assertThat(futureService.cause().getMessage(),containsString(expected));
		
	}
	
	@Then("^I check that \"([^\"]*)\" contains header \"([^\"]*)\" equals to \"([^\"]*)\"$")
	public void iCheckThatContainsHeaderEqualsDate(String key, String header, String expected) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.headers().contains(header),equalTo(true));
		assertThat(response.headers().get(header),equalTo(expected));
	}
	
	@When("^I send a \\\"([^\\s]*) ([^\"]*)\\\" with if-modified-since header \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithIfModifiedSinceHeaderGetting(String method, String path, String datekey, String resultKey) throws Exception {
		ZonedDateTime lastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) scenarioData.get(datekey)), ZoneId.of("GMT"));
		HttpHeaders headers = new DefaultHttpHeaders().add(HttpHeaderNames.IF_MODIFIED_SINCE, CACHE_DATE_PATTERN.format(lastModified));
		scenarioData.put(resultKey, send(null,method,path,null,headers).get());
	}
	
	@When("^I send a \\\"([^\\s]*) ([^\"]*)\\\" with if-modified-since header \"([^\"]*)\" minus (\\d+) seconds getting \"([^\"]*)\"$")
	public void iSendAWithIfModifiedSinceHeaderGettingMinusSeconds(String method, String path, String datekey, int seconds, String resultKey) throws Exception {
		ZonedDateTime lastModified = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) scenarioData.get(datekey)), ZoneId.of("GMT"));
		HttpHeaders headers = new DefaultHttpHeaders().add(HttpHeaderNames.IF_MODIFIED_SINCE, CACHE_DATE_PATTERN.format(lastModified.minus(seconds, ChronoUnit.SECONDS)));
		scenarioData.put(resultKey, send(null,method,path,null,headers).get());
	}
	
	@When("^I send a \\\"([^\\s]*) ([^\"]*)\\\" with empty if-modified-since header getting \"([^\"]*)\"$")
	public void iSendAWithEmptyIfModifiedSinceHeaderGetting(String method, String path, String resultKey) throws Exception {
		HttpHeaders headers = new DefaultHttpHeaders().add(HttpHeaderNames.IF_MODIFIED_SINCE, StringUtil.EMPTY_STRING);
		scenarioData.put(resultKey, send(null,method,path,null,headers).get());
	}
	
	@When("^I send a \\\"([^\\s]*) ([^\"]*)\\\" with authorizarion header \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithAuthorizarionHeaderGetting(String method, String path, String authorization, String resultKey) throws Exception {
		HttpHeaders headers = new DefaultHttpHeaders().add(HttpHeaderNames.AUTHORIZATION, authorization);
		scenarioData.put(resultKey, send(null,method,path,null,headers).get());
	}
	
	@Then("^I check that client has (\\d+) active connections$")
	public void iCheckThatClientHasActiveConnections(int count) throws Exception {
		Awaitility.await().until(client::activeConnections,equalTo(count));
		assertThat( client.activeConnections(),equalTo(count));
	}
	
	@Then("^I check that server certificate has name \"([^\"]*)\"$")
	public void iCheckThatServerCertificateHasName(String name) throws Exception {
		X509Certificate certificate = client.lastServerCertificate();
		assertThat(certificate.getSubjectDN().getName(),equalTo(name));
	}
	
	@Then("^I check that http error of \"([^\"]*)\" contains status code (\\d+)$")
	public void iCheckThatHttpErrorOfContainsStatusCode(String key, int code) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		HttpException error = (HttpException) futureService.cause();
		assertThat(error.code(),equalTo(code));
	}
	
	@Then("^I check that \"([^\"]*)\" has location header \"([^\"]*)\"$")
	public void iCheckThatHasLocationHeader(String key, String expectedLocation) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		String location = response.headers().get(HttpHeaderNames.LOCATION);
		assertThat(location,equalTo(expectedLocation));
	}
	
	@Then("^I check that \"([^\"]*)\" has cookie \"([^\"]*)\"$")
	public void iCheckThatHasCookie(String key, String name) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		String coockies = response.headers().get(HttpHeaderNames.SET_COOKIE);
		Cookie cookie = ClientCookieDecoder.LAX.decode(coockies);
		assertThat(cookie.name(),equalTo(name));
	}
	
	@When("^I close all client connections$")
	public void iCloseAllClientConnections() throws Exception {
		client.closeConnections().sync();
	}

	
}
