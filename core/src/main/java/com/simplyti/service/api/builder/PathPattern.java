package com.simplyti.service.api.builder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.netty.util.internal.StringUtil;

public class PathPattern {

	private final Pattern pattern;
	private final Map<String, Integer> pathParamNameToGroup;
	private final int literalCount;

	public PathPattern(Pattern pattern, Map<String, Integer> pathParamNameToGroup, int literalCount) {
		this.pattern=pattern;
		this.pathParamNameToGroup=pathParamNameToGroup;
		this.literalCount=literalCount;
	}
	
	public Pattern pattern() {
		return pattern;
	}
	
	public Map<String, Integer> pathParamNameToGroup() {
		return pathParamNameToGroup;
	}
	
	public int literalCount() {
		return literalCount;
	}

	public static PathPattern build(String uri) {
		StringBuilder pathTemplateBuilder = new StringBuilder();
		Builder<String, Integer> pathParamNameToGroupBuilder = ImmutableMap.<String, Integer>builder();
		AtomicInteger pathParamGroupCount = new AtomicInteger(1);
		AtomicReference<StringBuilder> pathParamNameRef = new AtomicReference<>();
		AtomicInteger literalCharsCount = new AtomicInteger();
		
		uri.replaceAll("^/+", StringUtil.EMPTY_STRING).replaceAll("/+$",  StringUtil.EMPTY_STRING).chars()
			.mapToObj(i -> (char) i)
			.forEach(character -> process(character, pathTemplateBuilder, pathParamNameToGroupBuilder, pathParamGroupCount,
				pathParamNameRef,literalCharsCount));
		
		pathTemplateBuilder.append("/?$").insert(0, "^/");
		Map<String, Integer> pathParamNameToGroup = pathParamNameToGroupBuilder.build();
		
		return new PathPattern(Pattern.compile(pathTemplateBuilder.toString()),pathParamNameToGroup,literalCharsCount.get());
	}
	
	private static void process(Character character, StringBuilder pathTemplateBuilder,
			Builder<String, Integer> pathParamNameToGroup, AtomicInteger pathParamGroupCount,
			AtomicReference<StringBuilder> pathParamNameRef, AtomicInteger literalCharsCount) {
		if (character.equals('{')) {
			pathParamNameRef.set(new StringBuilder());
		} else if (character.equals('}')) {
			StringBuilder pathParamName = pathParamNameRef.getAndSet(null);
			int regexInit = pathParamName.lastIndexOf(":");
			String regex;
			if (regexInit == -1) {
				regex = "[^/]*";
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
	}

}
