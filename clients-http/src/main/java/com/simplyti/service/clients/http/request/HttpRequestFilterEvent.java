package com.simplyti.service.clients.http.request;

import java.util.List;

import com.simplyti.service.filter.http.HttpRequestFilter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class HttpRequestFilterEvent {
	
	private final List<HttpRequestFilter> filters;

}
