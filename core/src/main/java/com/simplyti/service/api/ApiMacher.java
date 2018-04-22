package com.simplyti.service.api;

import java.util.regex.Matcher;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiMacher {
	
	private final ApiOperation<?,?> operation;
	private final Matcher matcher;
	
	public ApiOperation<?,?> operation() {
		return operation;
	}
	
	public Matcher matcher(){
		return matcher;
	}

}
