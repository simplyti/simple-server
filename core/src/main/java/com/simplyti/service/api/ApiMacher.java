package com.simplyti.service.api;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiMacher {
	
	private final ApiOperation<?,?> operation;
	private final Matcher matcher;
	private final Map<String,List<String>> parameters;
	
	public ApiOperation<?,?> operation() {
		return operation;
	}
	
	public Matcher matcher(){
		return matcher;
	}
	
	public Map<String,List<String>> parameters(){
		return parameters;
	}

}
