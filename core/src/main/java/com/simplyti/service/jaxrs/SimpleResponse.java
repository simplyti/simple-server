package com.simplyti.service.jaxrs;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class SimpleResponse extends Response {

	private final HttpResponseStatus status;
	private final HttpHeaders headers;

	public SimpleResponse(HttpResponseStatus status, HttpHeaders headers) {
		this.status=status;
		this.headers=headers;
	}
	
	public HttpResponseStatus responseStatus() {
		return status;
	}
	
	public HttpHeaders headers() {
		return headers;
	}

	@Override
	public int getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StatusType getStatusInfo() {
		return Status.fromStatusCode(status.code());
	}

	@Override
	public Object getEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T readEntity(Class<T> entityType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean bufferEntity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MediaType getMediaType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLanguage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLength() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getAllowedMethods() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, NewCookie> getCookies() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityTag getEntityTag() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getLastModified() {
		throw new UnsupportedOperationException();
	}

	@Override
	public URI getLocation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Link> getLinks() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasLink(String relation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Link getLink(String relation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Builder getLinkBuilder(String relation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MultivaluedMap<String, Object> getMetadata() {
		throw new UnsupportedOperationException();
	}

	@Override
	public MultivaluedMap<String, String> getStringHeaders() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeaderString(String name) {
		throw new UnsupportedOperationException();
	}

}
