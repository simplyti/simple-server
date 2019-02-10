package com.simplyti.service.jaxrs;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class SimpleResponseBuilder extends ResponseBuilder {
	
	private HttpResponseStatus status;
	private HttpHeaders headers;

	@Override
	public Response build() {
		return new SimpleResponse(status,headers);
	}

	@Override
	public ResponseBuilder clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder status(int status) {
		this.status=HttpResponseStatus.valueOf(status);
		return this;
	}

	@Override
	public ResponseBuilder status(int status, String reasonPhrase) {
		this.status=HttpResponseStatus.valueOf(status, reasonPhrase);
		return this;
	}

	@Override
	public ResponseBuilder entity(Object entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder entity(Object entity, Annotation[] annotations) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder allow(String... methods) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder allow(Set<String> methods) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder cacheControl(CacheControl cacheControl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder encoding(String encoding) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder header(String name, Object value) {
		if(headers==null) {
			this.headers=new DefaultHttpHeaders();
		}
		this.headers.add(name, value);
		return this;
	}

	@Override
	public ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder language(String language) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder language(Locale language) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder type(MediaType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder type(String type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder variant(Variant variant) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder contentLocation(URI location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder cookie(NewCookie... cookies) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder expires(Date expires) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder lastModified(Date lastModified) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder location(URI location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder tag(EntityTag tag) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder tag(String tag) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder variants(Variant... variants) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder variants(List<Variant> variants) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder links(Link... links) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder link(URI uri, String rel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseBuilder link(String uri, String rel) {
		throw new UnsupportedOperationException();
	}

}
