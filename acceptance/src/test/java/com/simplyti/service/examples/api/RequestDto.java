package com.simplyti.service.examples.api;

import java.util.Map;

import com.dslplatform.json.CompiledJson;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestDto {
	
	private final String body;
	private final Map<String,String> headers;
	private final Map<String,String> params;
	
	@CompiledJson
	public RequestDto(String body, Map<String,String> headers,  Map<String,String> params) {
		this.body=body;
		this.headers=headers;
		this.params=params;
	}

}
