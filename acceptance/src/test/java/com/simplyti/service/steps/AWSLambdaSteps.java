package com.simplyti.service.steps;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.simplyti.service.aws.lambda.AWSLambda;

import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.util.CharsetUtil;

public class AWSLambdaSteps {
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	private List<AWSLambda> lambdas;
	
	@After
	public void stop() {
		lambdas.stream().forEach(AWSLambda::stop);
	}
	
	@When("^I create the aws lambda \"([^\"]*)\" service \"([^\"]*)\"$")
	public void iCreateTheAwsLambdaService(String key, Class<? extends AWSLambda> clazz) throws Exception {
		AWSLambda lambda = clazz.newInstance();
		lambdas.add(lambda);
		scenarioData.put(key, lambda);
	}
	
	@When("^I invoke the aws lambda \"([^\"]*)\" with http method \"([^\"]*)\" and path \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iInvokeTheAwsLambdaWithHttpMethodAndPathGettingResponse(String key, String method, String path, String response) throws Exception {
		AWSLambda lambda = (AWSLambda) scenarioData.get(key);
		
		ImmutableMap<String, Object> event = ImmutableMap.<String, Object>builder()
			.put("httpMethod",method)
			.put("path",path)
			.build();
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		InputStream inputStream = new ByteArrayInputStream(JsonStream.serialize(event).getBytes(CharsetUtil.UTF_8));
		lambda.handleRequest(inputStream, outputStream, null);
		scenarioData.put(response, outputStream.toByteArray());
	}
	
	@Then("^I check that lambda result \"([^\"]*)\" has status code (\\d+)$")
	public void iCheckThatLambdaResultHasStatusCode(String key, int expected) throws Exception {
		byte[] result = (byte[]) scenarioData.get(key);
		int status = JsonIterator.deserialize(result).get("statusCode").toInt();
		assertThat(status,equalTo(expected));
	}
	
	@Then("^I check that lambda result \"([^\"]*)\" has body \"([^\"]*)\"$")
	public void iCheckThatLambdaResultHasBody(String key, String expected) throws Exception {
		byte[] result = (byte[]) scenarioData.get(key);
		String body = JsonIterator.deserialize(result).get("body").toString();
		assertThat(body,equalTo(expected));
	}
}
