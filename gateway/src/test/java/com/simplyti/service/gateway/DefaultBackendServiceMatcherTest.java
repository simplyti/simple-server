package com.simplyti.service.gateway;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

@RunWith(Parameterized.class)
public class DefaultBackendServiceMatcherTest {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { 
			{ "/myservice", "/other", "/myservice/resource" , "/other/resource" },
			{ "/myservice", "/other/", "/myservice/resource" , "/other/resource" },
			{ "/myservice", "/other", "/myservice/resource/" , "/other/resource/" },
			{ "/myservice", "/", "/myservice/resource" , "/resource" },
			{ "/myservice", "/", "/myservice/resource/" , "/resource/" },
			{ "/getstatus/{status}", "/status", "/getstatus/200" , "/status/200" },
			{ null, "/app/res", "/" , "/app/res" },
			{ null, "/app/res", "/subresource" , "/app/res/subresource" },
			{ null, "/app/res/", "/" , "/app/res/" },
			{ null, "/app/res/", "/3" , "/app/res/3" },
			{ "/static", "/other/static", "/static/file.js" , "/other/static/file.js" },
			});
	}

	private final String servicePath;
	private final String rewrite;
	private final String request;
	private final String expected;
	
	public DefaultBackendServiceMatcherTest(String servicePath, String rewrite, String request, String expected) {
        this.servicePath = servicePath;
        this.rewrite = rewrite;
        this.request=request;
        this.expected=expected;
    }

	@Test
	public void test() {
		BackendService service = new BackendService(null, null, servicePath, rewrite, false, null, Collections.emptyList());
		HttpRequest httpRrequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, request);

		BackendServiceMatcher matcher = new DefaultBackendServiceMatcher(service, httpRrequest.uri());

		assertTrue(matcher.matches());
		HttpRequest result = matcher.rewrite(httpRrequest);
		assertThat(result.uri()).isEqualTo(expected);
	}

}
