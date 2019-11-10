package com.simplyti.service.steps;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.simplyti.service.DefaultService;
import com.simplyti.service.api.serializer.json.Json;
import com.simplyti.service.builder.di.guice.GuiceService;
import com.simplyti.service.serializer.json.DslJsonSerializer;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.Future;
import io.vavr.control.Try;

public class OpenIdStepDefs {
	
	@Inject
	private List<Future<DefaultService>> services;
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	private ServiceBuilderStepDefs serviceSteps;
	
	@Given("^a selfsigned certificate \"([^\"]*)\"$")
	public void aSelfsignedCertificate(String key) throws Exception {
		SelfSignedCertificate cert = Try.of(()->new SelfSignedCertificate("simplyti.com",new SecureRandom(),2048)).get();
        scenarioData.put(key, cert);
	}
	
	@Given("^a JWT sign key \"([^\"]*)\" from self signed ccertificate \"([^\"]*)\"$")
	public void aJWTSignKeyFromSelfSignedCcertificate(String key, String cert) throws Exception {
		scenarioData.put(key, ((SelfSignedCertificate)scenarioData.get(cert)).key());
	}
	
	@Given("^a JWT sign key \"([^\"]*)\"$")
	public void aJWTSignKey(String key) throws Exception {
        scenarioData.put(key, MacProvider.generateKey());
	}
	
	@Given("^a valid JWT token \"([^\"]*)\" signed with alg \"([^\"]*)\" with key \"([^\"]*)\"$")
	public void aValidJWTTokenSignedWithAlgWithKey(String tokenKey, SignatureAlgorithm alg, String keyKey) throws Exception {
		Json json = new DslJsonSerializer(Collections.emptySet());
		String token = Jwts.builder()
				  .setSubject("Pepe")
				  .signWith((Key) scenarioData.get(keyKey),alg)
				  .setHeaderParam("kid","thekey")
				  .serializeToJsonWith(json::serialize)
				  .compact();
		scenarioData.put(tokenKey, token);
	}
	
	@Given("^an invalid JWT token \"([^\"]*)\" signed with alg \"([^\"]*)\"$")
	public void aInvalidJWTTokenSignedWithAlg(String tokenKey, SignatureAlgorithm alg) throws Exception {
		Json json = new DslJsonSerializer(Collections.emptySet());
		String token = Jwts.builder()
				  .setSubject("Pepe")
				  .signWith(MacProvider.generateKey(),alg)
				  .serializeToJsonWith(json::serialize)
				  .compact();
		scenarioData.put(tokenKey, token);
	}
	
	@Given("^a malformed JWT token \"([^\"]*)\"$")
	public void aMalformedJWTToken(String tokenKey) throws Exception {
		scenarioData.put(tokenKey, "this is a malformed token");
	}

	@When("^I send a \"([^\\s]*) ([^\"]*)\\\" with jwt token \"([^\"]*)\" getting \"([^\"]*)\"$")
	public void iSendAWithJwtTokenGetting(String method, String path, String tokenKey, String resultKey) throws Exception {
		String token = (String) scenarioData.get(tokenKey);
		HttpHeaders headers = new DefaultHttpHeaders();
		headers.set(HttpHeaderNames.AUTHORIZATION, "Bearer "+token);
		scenarioData.put(resultKey, serviceSteps.send(null,method,path,null,headers).get());
	}
	
	@Given("^an openid provider listening in port (\\d+) with sign certificate \"([^\"]*)\" and token endpoint \"([^\"]*)\"$")
	public void anOpenidProviderListeningInPortWithSignCertificateAndTokenEndpoint(int port, String keyKey,String tokenEndpoint) throws Exception {
		SelfSignedCertificate key = (SelfSignedCertificate) scenarioData.get(keyKey);
		Future<DefaultService> service = GuiceService.builder()
			    .securedPort(port)
			    .disableInsecurePort()
			    .withModule(new JsoniterModule())
			    .withModule(new FakeOpenIdModule(key,"/auth",tokenEndpoint,0,0))
			    	.build().start().await();
		 services.add(service);
	}
	
	@Given("^an openid provider listening in port (\\d+) with sign certificate \"([^\"]*)\", authorization endpoint \"([^\"]*)\" and token endpoint \"([^\"]*)\"$")
	public void anOpenidProviderListeningInPortWithSignCertificateAuthorizationEndpointTokenEndpoint(int port, String keyKey,String authEndpoint,String tokenEndpoint) throws Exception {
		SelfSignedCertificate key = (SelfSignedCertificate) scenarioData.get(keyKey);
		Future<DefaultService> service = GuiceService.builder()
			    .securedPort(port)
			    .disableInsecurePort()
			    .withModule(new JsoniterModule())
			    .withModule(new FakeOpenIdModule(key,authEndpoint,tokenEndpoint,0,0))
			    	.build().start().await();
		 services.add(service);
	}

	@Given("^an openid provider listening in port (\\d+) with sign certificate \"([^\"]*)\", authorization endpoint \"([^\"]*)\" and well-known service with (\\d+)ms of delay$")
	public void anOpenidProviderListeningInPortWithSignCertificateAuthorizationEndpointMsOfDelay(int port, String keyKey,String authEndpoint, int wellKnownDelay) throws Exception {
		SelfSignedCertificate key = (SelfSignedCertificate) scenarioData.get(keyKey);
		Future<DefaultService> service = GuiceService.builder()
			    .securedPort(port)
			    .disableInsecurePort()
			    .withModule(new JsoniterModule())
			    .withModule(new FakeOpenIdModule(key,authEndpoint,"/token",wellKnownDelay,0))
			    	.build().start().await();
		 services.add(service);
	}
	
	@Given("^an openid provider listening in port (\\d+) with sign certificate \"([^\"]*)\", authorization endpoint \"([^\"]*)\", token endpoint \"([^\"]*)\" and jwks service with (\\d+)ms of delay$")
	public void anOpenidProviderListeningInPortWithSignCertificateAuthorizationEndpointAndTokenEndpointWithMsOfDelay(int port, String keyKey,String authEndpoint,String tokenEndpoint, int jwksDelay) throws Exception {
		SelfSignedCertificate key = (SelfSignedCertificate) scenarioData.get(keyKey);
		Future<DefaultService> service = GuiceService.builder()
			    .securedPort(port)
			    .disableInsecurePort()
			    .withModule(new JsoniterModule())
			    .withModule(new FakeOpenIdModule(key,authEndpoint,tokenEndpoint,0,jwksDelay))
			    	.build().start().await();
		 services.add(service);
	}
	
}
