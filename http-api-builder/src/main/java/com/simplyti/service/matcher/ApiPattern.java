package com.simplyti.service.matcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface ApiPattern {
	
	public int variableCount();
	public int literalCharsCount();
	public Map<String, Integer> pathParamNameToGroup();
	public ApiMatcher matcher(String value);

	public static ApiPattern build(String path) {
		if(path.length()==1 && path.charAt(0)== '/'){
			return new NoVariableApiPattern(path);
		}
		
		StringBuilder pathTemplateBuilder = new StringBuilder();
		AtomicReference<StringBuilder> pathParamNameRef = new AtomicReference<>();
		Map<String, Integer> pathParamNameToGroup = new HashMap<>();
		AtomicInteger pathParamGroupCount = new AtomicInteger(1);
		AtomicInteger literalCharsCount = new AtomicInteger();
		
		path.trim().chars().mapToObj(i -> (char) i)
			.forEach(character->{
				if (character.equals('{')) {
					pathParamNameRef.set(new StringBuilder());
				} else if (character.equals('}')) {
					StringBuilder pathParamName = pathParamNameRef.getAndSet(null);
					String regex;
					int regexInit = pathParamName.lastIndexOf(":");
					if (regexInit == -1) {
						regex = "[^/]+";
					} else {
						regex = pathParamName.substring(regexInit + 1);
						pathParamName.replace(regexInit, pathParamName.length(), "");
					}
					pathTemplateBuilder.append("(" + regex + ")");
					pathParamNameToGroup.put(pathParamName.toString(), pathParamGroupCount.getAndIncrement());
				} else if (pathParamNameRef.get() == null) {
					pathTemplateBuilder.append(character);
					literalCharsCount.incrementAndGet();
				} else {
					pathParamNameRef.get().append(character);
				}
			});

		if(pathParamNameToGroup.isEmpty()) {
			return new NoVariableApiPattern(normalize(path));
		} else {
			return new VariableApiPattern(normalize(pathTemplateBuilder.toString()),pathParamNameToGroup,literalCharsCount.get());
		}
		
	}

	public static String normalize(String path) {
		if(path.length()==1 && path.charAt(0)== '/'){
			return path;
		}
		
		String normalized;
		if(path.charAt(0)!='/') {
			normalized = "/"+path;
		} else {
			normalized = path;
		}
		
		if(path.charAt(path.length()-1)=='/') {
			return normalized.substring(0,path.length()-1);
		} else{
			return normalized;
		}
	}
	
}
