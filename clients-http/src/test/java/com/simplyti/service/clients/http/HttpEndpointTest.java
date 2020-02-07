package com.simplyti.service.clients.http;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class HttpEndpointTest {
	
	@Parameters(name ="{index}: Http endpoint {0} must contain schema {1}, ssi {2}, host {3}, port {4} and path {5}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { "http://10.0.0.1", "http", false, "10.0.0.1", 80, "/" },
                 { "https://10.0.0.1", "https", true, "10.0.0.1", 443, "/" },
                 { "https://10.0.0.1:8443", "https", true, "10.0.0.1", 8443, "/" },
                 { "http://10.0.0.1:8080/path", "http", false, "10.0.0.1", 8080, "/path" },
                 { "10.0.0.1", "http", false, "10.0.0.1", 80, "/" },
                 { "httpbin", "http", false, "httpbin", 80, "/" },
                 { "httpbin/path", "http", false, "httpbin", 80, "/path" },
                 { "httpbin/path:80", "http", false, "httpbin", 80, "/path:80" },
                 { "httpbin:443/path:80", "http", false, "httpbin", 443, "/path:80" },
           });
    }

    private final String endpoint;
    private final String schema;
	private final boolean ssl;
	private final String host;
	private final int port;
	private final String path;

    public HttpEndpointTest(String endpoint, String schema, boolean ssl, String host, int port, String path){
    		this.endpoint=endpoint;
    		this.schema=schema;
    		this.ssl=ssl;
    		this.host=host;
    		this.port=port;
    		this.path=path;
    }

    @Test
    public void test() {
    		HttpEndpoint result = HttpEndpoint.of(endpoint);
        assertThat(result.schema().name(),equalTo(schema));
        assertThat(result.schema().ssl(),equalTo(ssl));
        assertThat(result.address().host(),equalTo(host));
        assertThat(result.address().port(),equalTo(port));
        assertThat(result.path(),equalTo(path));
    }

}
