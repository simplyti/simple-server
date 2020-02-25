package com.simplyti.server.http.api.pattern;

import java.util.Map;

import com.google.re2j.Pattern;

public class VariableApiPattern implements ApiPattern {

	private final Pattern pattern;
	private final int variableCount;
	private final int literalCharsCount;
	private final Map<String, Integer> pathParamNameToGroup;

	public VariableApiPattern(String regex, Map<String, Integer> pathParamNameToGroup, int literalCharsCount) {
		this.pattern = Pattern.compile("^"+regex+"/?$");
		this.variableCount = pathParamNameToGroup.size();
		this.literalCharsCount=literalCharsCount;
		this.pathParamNameToGroup=pathParamNameToGroup;
	}

	@Override
	public int variableCount() {
		return variableCount;
	}
	
	@Override
	public int literalCharsCount() {
		return literalCharsCount;
	}

	@Override
	public ApiMatcher matcher(String value) {
		return new RegexApiMatcher(this.pattern,value);
	}

	@Override
	public Map<String, Integer> pathParamNameToGroup() {
		return pathParamNameToGroup;
	}

}
