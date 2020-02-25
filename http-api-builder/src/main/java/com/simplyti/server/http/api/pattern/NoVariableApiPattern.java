package com.simplyti.server.http.api.pattern;

import java.util.Collections;
import java.util.Map;

public class NoVariableApiPattern implements ApiPattern {
	
	private static final int ZERO = 0;
	
	private final String path;

	public NoVariableApiPattern(String path) {
		this.path=path;
	}

	@Override
	public int variableCount() {
		return ZERO;
	}
	
	@Override
	public int literalCharsCount() {
		return path.length();
	}

	@Override
	public ApiMatcher matcher(String value) {
		return new EqualsApiMatcher(this.path,ApiPattern.normalize(value));
	}

	@Override
	public Map<String, Integer> pathParamNameToGroup() {
		return Collections.emptyMap();
	}


}
