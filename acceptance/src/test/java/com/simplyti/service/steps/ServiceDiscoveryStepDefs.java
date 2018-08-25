package com.simplyti.service.steps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.simplyti.service.client.SimpleHttpResponse;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.discovery.TestServiceDiscovery;

import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

public class ServiceDiscoveryStepDefs {
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@After
	public void reset() {
		TestServiceDiscovery.reset();
	}
	
	@When("^I create a service with path \"([^\"]*)\" and backend \"([^\"]*)\"$")
	public void iCreateAServiceWithPathAndBackend(String path, String target) throws Exception {
		TestServiceDiscovery.getInstance().addService(null, null, path, HttpEndpoint.of(target));
	}
	
	@When("^I create a service with method \"([^\"]*)\" with path \"([^\"]*)\" and backend \"([^\"]*)\"$")
	public void iCreateAServiceWithMethodWithPathAndBackend(String method, String path, String target) throws Exception {
		TestServiceDiscovery.getInstance().addService(null, HttpMethod.valueOf(method), path, HttpEndpoint.of(target));
	}
	
	@When("^I create a service with host \"([^\"]*)\" with path \"([^\"]*)\" and backend \"([^\"]*)\"$")
	public void iCreateAServiceWithHostWithPathAndBackend(String host, String path, String target) throws Exception {
		TestServiceDiscovery.getInstance().addService(host, null, path, HttpEndpoint.of(target));
	}
	
	@When("^I create a service with path \"([^\"]*)\"$")
	public void iCreateAServiceWithPath(String path) throws Exception {
		TestServiceDiscovery.getInstance().addService(null, null, path, null);
	}
	
	@When("^I create a service with backend \"([^\"]*)\"$")
	public void iCreateAServiceWithBackend(String target) throws Exception {
		TestServiceDiscovery.getInstance().addService(null, null, null, HttpEndpoint.of(target));
	}

	@When("^I create a service with host \"([^\"]*)\" and backend \"([^\"]*)\"$")
	public void iCreateAServiceWithHostAndBackend(String host, String target) throws Exception {
		TestServiceDiscovery.getInstance().addService(host, null, null, HttpEndpoint.of(target));
	}
	
	@Then("^I check that \"([^\"]*)\" has status code (\\d+) with location starting with \"([^\"]*)\"$")
	public void iCheckThatHasStatusCodeWithLocation(String key, int code, String location) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.status(),equalTo(code));
		assertThat(response.headers().get(HttpHeaderNames.LOCATION), startsWith(location));
	}
	
	@Then("^I check that \"([^\"]*)\" has location header starting with \"([^\"]*)\"$")
	public void iCheckThatHasLocationHeaderStartingWith(String key, String location) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		assertThat(response.headers().get(HttpHeaderNames.LOCATION), startsWith(location));
	}
	
	
	@Then("^I check that \"([^\"]*)\" redirect location contains params:$")
	public void iCheckThatRedirectLocationContainsParams(String key, Map<String,String> expectedParams) throws Exception {
		SimpleHttpResponse response = (SimpleHttpResponse) scenarioData.get(key);
		QueryStringDecoder decoder = new QueryStringDecoder(response.headers().get(HttpHeaderNames.LOCATION));
		Map<String, List<String>> params = decoder.parameters();
		for(Entry<String, String> expectedParam:expectedParams.entrySet()) {
			assertThat(params,hasKey(expectedParam.getKey()));
			assertThat(params.get(expectedParam.getKey()),hasItem(expectedParam.getValue()));
		}
	}
	
}
