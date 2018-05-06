package com.simplyti.service.steps;

import java.security.Key;
import java.util.Map;

import javax.inject.Inject;

import com.simplyti.service.auth.JWTAuthModule;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;

public class JWTAuthStepDefs {
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	private ServiceBuilderStepDefs serviceSteps;

	@Given("^a JWT sign key \"([^\"]*)\"$")
	public void aJWTSignKey(String key) throws Exception {
		scenarioData.put(key, MacProvider.generateKey());
	}

	@Given("^a valid JWT token \"([^\"]*)\" signed with alg \"([^\"]*)\" with key \"([^\"]*)\"$")
	public void aValidJWTTokenSignedWithAlgWithKey(String tokenKey, SignatureAlgorithm alg, String keyKey) throws Exception {
		String token = Jwts.builder()
				  .setSubject("Pepe")
				  .signWith(alg, (Key) scenarioData.get(keyKey))
				  .compact();
		scenarioData.put(tokenKey, token);
	}
	
	@Given("^a invalid JWT token \"([^\"]*)\" signed with alg \"([^\"]*)\"$")
	public void aInvalidJWTTokenSignedWithAlg(String tokenKey, SignatureAlgorithm alg) throws Exception {
		String token = Jwts.builder()
				  .setSubject("Pepe")
				  .signWith(alg, MacProvider.generateKey())
				  .compact();
		scenarioData.put(tokenKey, token);
	}
	
	@Given("^a malformed JWT token \"([^\"]*)\"$")
	public void aMalformedJWTToken(String tokenKey) throws Exception {
		scenarioData.put(tokenKey, "this is a malformed token");
	}

	
	@Given("^a JWT auth module \"([^\"]*)\" with key \"([^\"]*)\"$")
	public void aJWTAuthModuleWithKey(String moduleKey, String keyKey) throws Exception {
		Key key = (Key) scenarioData.get(keyKey);
		scenarioData.put(moduleKey, new JWTAuthModule(key));
	}

	@When("^I send a \"([^\\s]*) ([^\"]*)\\\" with jwt token \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithJwtTokenGetting(String method, String path, String tokenKey, String resultKey) throws Exception {
		String token = (String) scenarioData.get(tokenKey);
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.set(HttpHeaderNames.AUTHORIZATION, "Bearer "+token);
		scenarioData.put(resultKey, serviceSteps.send(null,method,path,null,headers).get());
	}
	
}
