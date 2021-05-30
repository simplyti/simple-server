package com.simplyti.server.http.api.request;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.simplyti.server.http.api.context.ApiContext;
import com.simplyti.server.http.api.operations.ApiOperation;
import com.simplyti.service.matcher.ApiMatcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent=true)
@AllArgsConstructor
public class ApiMatchRequest {
	
	private final ApiOperation<? extends ApiContext> operation;
	private final Map<String,List<String>> parameters;
	private final ApiMatcher matcher;
	
	public String group(int group) {
		return matcher.group(group);
	}

	public Collection<String> parameterNames() {
		return parameters.keySet();
	}

}
