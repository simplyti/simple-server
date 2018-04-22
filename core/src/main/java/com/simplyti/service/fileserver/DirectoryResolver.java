package com.simplyti.service.fileserver;

import java.util.regex.Matcher;

public interface DirectoryResolver {

	String resolve(Matcher matcher);

	static DirectoryResolver literal(String directory) {
		return new LieralDirectoryResolver(directory);
	}
	
	static class LieralDirectoryResolver implements DirectoryResolver {

		private final String directory;

		private LieralDirectoryResolver(String directory) {
			this.directory=directory;
		}

		@Override
		public String resolve(Matcher matcher) {
			return directory;
		}
		
	}

}
