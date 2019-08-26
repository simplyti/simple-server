package com.simplyti.service.steps;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.awaitility.Awaitility;

import com.google.common.base.Splitter;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.http.HttpClient;
import com.simplyti.service.clients.http.HttpEndpoint;
import com.simplyti.service.clients.k8s.KubeClient;
import com.simplyti.service.clients.k8s.secrets.domain.Secret;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class K8sServiceDiscoveryStepDefs {
	
	private static final Endpoint LOCAL_ENDPOINT = HttpEndpoint.of("http://localhost:8080");
	private static final String CA_KEY = "SHA1withRSA";

	@Inject
	private KubeClient k8sClient;
	
	@Inject
	private Map<String,Object> scenarioData;
	
	@Inject
	@Named("scenario")
	private HttpClient httpClient;
	
	@Then("^I check that exist (\\d+) gateway services$")
	public void iCheckThatExistIngress(int count) throws Exception {
		Awaitility.await().until(this::gatewayServices,hasSize(count));
		List<Any> response = gatewayServices();
		assertThat(response,hasSize(count));
	}
	
	@Then("^I check that exist a gateway service with targets \"([^\"]*)\"$")
	public void iCheckThatExistAnIngressWithTargets(String strTargets) throws Exception {
		List<String> expectedTargets = Splitter.on(',').splitToList(strTargets)
				.stream().map(str->str.replace("${local.address}", K8sClientStepDefs.getLocalAddress()))
				.collect(Collectors.toList());
		Awaitility.await().atMost(30,TimeUnit.SECONDS).until(()->{
			List<Any> ingresses = gatewayServices();
			assertThat(ingresses,hasSize(1));
			List<String> endpoints = ingresses.get(0).get("endpoints").asList().stream().map(any->any.get("url").toString()).collect(Collectors.toList());
			assertThat(endpoints,hasSize(expectedTargets.size()));
			return endpoints.containsAll(expectedTargets);
		});
	}
	
	@Then("^I check that exist a gateway service without targets$")
	public void iCheckThatExistAnIngressWithoutTargets() throws Exception {
		Awaitility.await().atMost(30,TimeUnit.SECONDS).until(()->{
			List<Any> ingresses = gatewayServices();
			assertThat(ingresses,hasSize(1));
			return ingresses.get(0).get("endpoints").asList().isEmpty();
		});
	}
	
	private List<Any> gatewayServices() throws InterruptedException, ExecutionException{
		FullHttpResponse response = httpClient.request().withEndpoint(LOCAL_ENDPOINT)
			.get("/_api/gateway/services")
			.fullResponse().get();
		
		byte[] data = new byte[response.content().readableBytes()];
		response.content().readBytes(data);
		response.release();
		List<Any> body = JsonIterator.deserialize(data).asList();
		return body;
	}
	
	@Given("^a key pair \"([^\"]*)\" with algorithm \"([^\"]*)\" and bits (\\d+)$")
	public void aKeyPairWithAlgorithmAndBits(String keyName, String algorithm, int bits) throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
        keyGen.initialize(bits, new SecureRandom());
        scenarioData.put(keyName, keyGen.generateKeyPair());
	}
	
	@Given("^a certificate \"([^\"]*)\" autosigned with key \"([^\"]*)\" with common name \"([^\"]*)\"$")
	public void aCertificateAutosignedWithKeyWithCommonName(String certName, String keyName, String commonName) throws Exception {
		KeyPair keypair = (KeyPair) scenarioData.get(keyName);
		X509CertInfo info = new X509CertInfo();
        X500Name owner = new X500Name("CN=" + commonName);
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new BigInteger(64, new Random())));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);
		info.set(X509CertInfo.VALIDITY, new CertificateValidity(new Date(System.currentTimeMillis() - 86400000L * 365),new Date(253402300799000L)));
        info.set(X509CertInfo.KEY, new CertificateX509Key(keypair.getPublic()));
        info.set(X509CertInfo.ALGORITHM_ID,new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha1WithRSAEncryption_oid)));
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(keypair.getPrivate(), CA_KEY);
        info.set(CertificateAlgorithmId.NAME + '.' + CertificateAlgorithmId.ALGORITHM, cert.get(X509CertImpl.SIG_ALG));
        cert = new X509CertImpl(info);
        cert.sign(keypair.getPrivate(), CA_KEY);
        scenarioData.put(certName, cert);
	}
	
	@When("^I create a server certificate secret in namespace \"([^\"]*)\" with name \"([^\"]*)\", key \"([^\"]*)\" and cert \"([^\"]*)\"$")
	public void iCreateAServerCertificateSecretWithNameInNamespaceWithKeyAndCert(String namespace,String name,  String keyKey, String certKey) throws Exception {
		KeyPair keypair = (KeyPair) scenarioData.get(keyKey);
		X509Certificate cert = (X509Certificate) scenarioData.get(certKey);
		ByteBuf certBuff = Base64.encode(Unpooled.wrappedBuffer(cert.getEncoded()));
		String certStr = "-----BEGIN CERTIFICATE-----\n"+certBuff.toString(CharsetUtil.UTF_8)+"\n-----END CERTIFICATE-----";
		certBuff.release();
		ByteBuf privKeyBuff = Base64.encode(Unpooled.wrappedBuffer(keypair.getPrivate().getEncoded()));
		String privKeyStr = "-----BEGIN PRIVATE KEY-----\n"+privKeyBuff.toString(CharsetUtil.UTF_8)+"\n-----END PRIVATE KEY-----";
		privKeyBuff.release();
		
		Future<Secret> result = k8sClient.secrets().namespace(namespace).builder()
		    		.withName(name)
		    		.withData("tls.key", privKeyStr)
		    		.withData("tls.crt", certStr)
		    		.build().await();
	   assertTrue(result.isSuccess());
	   assertThat(result.getNow(),notNullValue());
	}

	@When("^I wait (\\d+) milliseconds$")
	public void iWaitMiliseconds(int millis) throws Exception {
	    Thread.sleep(millis);
	}
	
}
