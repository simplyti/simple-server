package com.simplyti.service.steps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.simplyti.service.aws.lambda.AWSLambda;

import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.netty.util.CharsetUtil;

public class AWSLambdaSteps {

	@Inject
	private Map<String, Object> scenarioData;

	@Inject
	private List<AWSLambda> lambdas;

	@After
	public void stop() {
		lambdas.stream().forEach(AWSLambda::stop);
	}
	
	@ParameterType(".*")
    public AWSLambda lambda(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	return (AWSLambda) Class.forName(className.replaceAll("^\"", "").replaceAll("\"$", "")).newInstance();
    }

	@When("I create the aws lambda {string} service {lambda}")
	public void iCreateTheAwsLambdaService(String key, AWSLambda lambda) throws Exception {
		lambdas.add(lambda);
		scenarioData.put(key, lambda);
	}

	@When("^I invoke the aws lambda \"([^\"]*)\" with http method \"([^\"]*)\" and path \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iInvokeTheAwsLambdaWithHttpMethodAndPathGettingResponse(String key, String method, String path,
			String response) throws Exception {
		ImmutableMap<String, Object> event = ImmutableMap.<String, Object>builder().put("httpMethod", method)
				.put("path", path).build();

		invokeLambda(key,event,response);
	}

	private void invokeLambda(String key, ImmutableMap<String, Object> event, String response) throws IOException {
		AWSLambda lambda = (AWSLambda) scenarioData.get(key);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		InputStream inputStream = new ByteArrayInputStream(JsonStream.serialize(event).getBytes(CharsetUtil.UTF_8));
		lambda.handleRequest(inputStream, outputStream, null);
		scenarioData.put(response, outputStream.toByteArray());
	}

	@When("^I invoke the aws lambda \"([^\"]*)\" with http method \"([^\"]*)\", path \"([^\"]*)\", query parameters \"([^\"]*)\" and body \"([^\"]*)\" getting response \"([^\"]*)\"$")
	public void iInvokeTheAwsLambdaWithHttpMethodPathQueryParametersAndBodyGettingResponse(String key, String method,
			String path, String queryParams, String body, String response) throws Exception {
		ImmutableMap<String, Object> event = ImmutableMap.<String, Object>builder()
				.put("httpMethod", method)
				.put("path", path).put("body", body)
				.put("queryStringParameters", Splitter.on('&').withKeyValueSeparator('=').split(queryParams)).build();

		invokeLambda(key,event,response);
	}

	@When("^I invoke the aws lambda \"([^\"]*)\" with http method \"([^\"]*)\", path \"([^\"]*)\" and empty query parameters getting response \"([^\"]*)\"$")
	public void iInvokeTheAwsLambdaWithHttpMethodPathAndQueryParametersGettingResponse(String key, String method,
			String path, String response) throws Exception {
		ImmutableMap<String, Object> event = ImmutableMap.<String, Object>builder()
				.put("httpMethod", method)
				.put("path",path)
				.put("queryStringParameters", Collections.emptyMap()).build();

		invokeLambda(key,event,response);
	}

	@Then("^I check that lambda result \"([^\"]*)\" has status code (\\d+)$")
	public void iCheckThatLambdaResultHasStatusCode(String key, int expected) throws Exception {
		byte[] result = (byte[]) scenarioData.get(key);
		int status = JsonIterator.deserialize(result).get("statusCode").toInt();
		assertThat(status, equalTo(expected));
	}

	@Then("^I check that lambda result \"([^\"]*)\" has body \"([^\"]*)\"$")
	public void iCheckThatLambdaResultHasBody(String key, String expected) throws Exception {
		byte[] result = (byte[]) scenarioData.get(key);
		String body = JsonIterator.deserialize(result).get("body").toString();
		assertThat(body, equalTo(expected));
	}
}
