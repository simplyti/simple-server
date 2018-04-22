package com.simplyti.service.steps;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;

import com.google.inject.Module;
import com.jayway.awaitility.Awaitility;
import com.simplyti.service.Service;
import com.simplyti.service.TestServerStopHookModule;
import com.simplyti.service.api.builder.ApiProvider;
import com.simplyti.service.builder.ServiceBuilder;
import com.simplyti.service.client.SimpleHttpClient;
import com.simplyti.service.client.SimpleHttpResponse;
import com.simplyti.service.clients.http.exception.HttpException;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.StringUtil;
import io.vavr.control.Try;

import static io.vavr.control.Try.run;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ServiceBuilderStepDefs {
	
	private static final DateTimeFormatter CACHE_DATE_PATTERN = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	private String tempDir = System.getProperty("user.dir")+"/target/tempDir";
	
	@Inject
	private  SimpleHttpClient client;
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	private List<Future<Service>> services;
	
	@When("^I start a service \"([^\"]*)\" with API \"([^\"]*)\"$")
	public void iStartAServiceWithAPI(String key, Class<?extends ApiProvider> api) throws Exception {
		Future<Service> futureService = Service.builder()
			.withLog4J2Logger()
			.withApi(api)
			.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("^I start a service \"([^\"]*)\"$")
	public void iStartAService(String key) throws Exception {
		Future<Service> futureService = Service.builder()
				.withLog4J2Logger()
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("^I start a service \"([^\"]*)\" with module \"([^\"]*)\"$")
	public void iStartAServiceWithModule(String key, Class<? extends Module> module) throws Exception {
		Future<Service> futureService = Service.builder()
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
	
	@When("^I send files \"([^\"]*)\" to \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendFileGetting(List<String> fileNames, String path, String resultKey) throws Exception {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path);
		HttpDataFactory factory = new DefaultHttpDataFactory(false);
		HttpPostRequestEncoder encoder = new HttpPostRequestEncoder(factory,request, true);
		
		for(String fileName:fileNames){
			Path filePath = Paths.get(fileName.replaceAll("#tempdir", tempDir));
			encoder.addBodyFileUpload("test.txt", filePath.toFile(), "text/plain", false);
		}
		
		request = encoder.finalizeRequest();
		scenarioData.put(resultKey, client.send(request,encoder).get());
	}

	
	@When("^I start a service \"([^\"]*)\" with file serve \"([^\"]*)\" on \"([^\"]*)\"$")
	public void iStartAServiceWithFileServeOn(String key, String directory, String path) throws Exception {
		String thedir = directory.replaceAll("#tempdir", tempDir);
		Future<Service> futureService = Service.builder()
				.withLog4J2Logger()
				.fileServe(path,thedir)
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("^I start a service \"([^\"]*)\" with file serve \"([^\"]*)\" on \"([^\"]*)\" and API \"([^\"]*)\"$")
	public void iStartAServiceWithFileServeOnAndAPI(String key, String directory, String path, Class<?extends ApiProvider> api) throws Exception {
		String thedir = directory.replaceAll("#tempdir", tempDir);
		Future<Service> futureService = Service.builder()
				.withLog4J2Logger()
				.fileServe(path,thedir)
				.withApi(api)
				.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}
	
	@When("^I stop server \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iStopServerGetting(String key, String resultKey) throws Exception {
		@SuppressWarnings("unchecked")
		Future<Service> futureService = (Future<Service>) scenarioData.get(key);
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
		ServiceBuilder builder = Service.builder();
		options.forEach(option->{
			if(option.get("option").equals("withLog4J2Logger")) {
				builder.withLog4J2Logger();
			}else if(option.get("option").equals("insecuredPort")) {
				builder.insecuredPort(Integer.parseInt(option.get("value")));
			}else if(option.get("option").equals("securedPort")) {
				builder.securedPort(Integer.parseInt(option.get("value")));
			}else if(option.get("option").equals("withApi")) {
				builder.withApi((Class<? extends ApiProvider>)Try.of(()->Class.forName(option.get("value"))).get());
			}else if(option.get("option").equals("withModule")) {
				String value = option.get("value");
				if(value.startsWith("#")) {
					builder.withModule((Module)scenarioData.get(value));
				}else {
					builder.withModule((Class<? extends Module>)Try.of(()->Class.forName(value)).get());
				}
			}else {
				throw new IllegalArgumentException("Unknown option "+option);
			}
		});
		Future<Service> futureService = builder.build().start();
		services.add(futureService);
		scenarioData.put(key, futureService);
	}

	@After
	public void stop() {
		services.stream()
			.forEach(futureService->Awaitility.await().until(futureService::isDone));
		
		services.stream().filter(Future::isSuccess)
			.map(Future::getNow)
			.forEach(service->run(()->service.stop().await()));
	}
	
	@When("^I check that \"([^\"]*)\" has been shutted down$")
	public void iCheckThatHasBeenShuttedDown(String key) throws Exception {
		@SuppressWarnings("unchecked")
		Service service = ((Future<Service>) scenarioData.get(key)).getNow();
		Future<Void> stopFuture = service.stopFuture();
		Awaitility.await().until(stopFuture::isDone);
		assertThat(stopFuture.isSuccess(),equalTo(true));
	}
	
	@When("^I try to send a \"([^\\s]*) ([^\"]*)\" getting \"([^\"]*)\"$")
	public void iTryToSendAGetting(String method, String path, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,null,null).await());
	}

	@When("^I send an HTTP \"([^\"]*)\" \"([^\\s]*) ([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAHttpVersionGetting(String version, String method, String path, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(HttpVersion.valueOf("HTTP/"+version),method,path,null,null).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAGetting(String method, String path, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,null,null).get());
	}
	
	@When("^I send a \"([^\\s]*) ([^\"]*)\" with body \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithBodyGetting(String method, String path,String body, String resultKey) throws Exception {
		scenarioData.put(resultKey, send(null,method,path,body,null).get());
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
	
	private Future<SimpleHttpResponse> send(HttpVersion version,String method, String path, String body, HttpHeaders headers) {
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
			throw new UnsupportedOperationException();
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
	
	@Then("^I check that error \"([^\"]*)\" contains message \"([^\"]*)\"$")
	public void iCheckThatErrorContainsMessage(String key, String expected) throws Exception {
	    Throwable error = (Throwable) scenarioData.get(key);
	    assertThat(error,notNullValue());
	    assertThat(error.getMessage(),equalTo(expected));
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
		assertThat(futureService.isSuccess(),equalTo(true));
	}
	
	@Then("^I check that \"([^\"]*)\" is failure$")
	public void iCheckThatIsFailure(String key) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		Awaitility.await().until(futureService::isDone);
		assertThat(futureService.isSuccess(),equalTo(false));
	}
	
	@Then("^I check that error failure message of \"([^\"]*)\" is \"([^\"]*)\"$")
	public void iCheckThatErrorFailureMessageOfIs(String key, String expected) throws Exception {
		Future<?> futureService = (Future<?>) scenarioData.get(key);
		assertThat(futureService.isSuccess(),equalTo(false));
		assertThat(futureService.cause().getMessage(),equalTo(expected));
		
	}
	
	@Then("^I check that \"([^\"]*)\" contains header \"([^\"]*)\" equals to date \"([^\"]*)\"$")
	public void iCheckThatContainsHeaderEqualsToDate(String key, String header, String datekey) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.headers().contains(header),equalTo(true));
		Instant date = Instant.from(CACHE_DATE_PATTERN.parse(response.headers().get(header)));
		assertThat(date.toEpochMilli(),equalTo(scenarioData.get(datekey)));
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
	

}
