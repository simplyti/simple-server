package com.simplyti.service.steps;

import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.discovery.TestServiceDiscovery;

import cucumber.api.java.After;
import cucumber.api.java.en.When;
import io.netty.handler.codec.http.HttpMethod;

public class ServiceDiscoveryStepDefs {
	
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


}
