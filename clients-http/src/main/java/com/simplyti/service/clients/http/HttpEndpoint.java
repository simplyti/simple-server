package com.simplyti.service.clients.http;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.simplyti.service.clients.Address;
import com.simplyti.service.clients.Endpoint;
import com.simplyti.service.clients.Schema;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper=true,of= {})
public class HttpEndpoint extends Endpoint{
	
	private static final Pattern PATTERN = Pattern.compile("(http|https)?(://)?([^:/]+):?(\\d+)?");
	public static final Schema HTTP_SCHEMA = new Schema("http", false, 80);
	public static final Schema HTTPS_SCHEMA = new Schema("https", true, 443);
	
	private final String path;

	public HttpEndpoint(Schema schema, Address address, String path) {
		super(schema,address);
		this.path=path;
	}
	
	public static HttpEndpoint of(String endpoint) {
		int schemaDelim = endpoint.indexOf("://");
		final int pathBegin;
		if(schemaDelim==-1) {
			pathBegin = endpoint.indexOf('/');
		}else {
			pathBegin = endpoint.indexOf('/', schemaDelim+3);
		}
		
		final String path;
		final String hostPart;
		if(pathBegin==-1) {
			path = "/";
			hostPart = endpoint;
		}else {
			path = endpoint.substring(pathBegin);
			hostPart = endpoint.substring(0, pathBegin);
		}
		
		Matcher matcher = PATTERN.matcher(hostPart);
		checkArgument(matcher.matches());
		Optional<String> optionalSchema = Optional.ofNullable(matcher.group(1));
		Schema schema = optionalSchema
				.map(str->str.equals(HTTPS_SCHEMA.name())?HTTPS_SCHEMA:HTTP_SCHEMA)
				.orElse(HTTP_SCHEMA);
		
		String host = matcher.group(3);
		int port = Optional.ofNullable(matcher.group(4))
				.map(Integer::parseInt)
				.orElseGet(schema::defaultPort);
		return new HttpEndpoint(schema,new Address(host, port),path);
	}

}
