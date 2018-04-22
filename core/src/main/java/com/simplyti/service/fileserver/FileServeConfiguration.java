package com.simplyti.service.fileserver;

import java.util.regex.Pattern;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileServeConfiguration {
	
	private final Pattern pattern;
	private final DirectoryResolver directoryResolver;
	
	public DirectoryResolver directoryResolver() {
		return directoryResolver;
	}

	public Pattern pattern() {
		return pattern;
	}

}
