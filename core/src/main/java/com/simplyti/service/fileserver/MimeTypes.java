package com.simplyti.service.fileserver;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {

	private static final String DEFAULT_TYPE = "application/octet-stream";
	private static final Map<String,String> TYPES = new HashMap<>();
	private static final String APPLICATION_JSON = "application/json";
	private static final String APPLICATION_JAVASCRIPT = "application/javascript";
	private static final String APPLICATION_FONTOBJECT = "application/vnd.ms-fontobject";
	private static final String APPLICATION_FONT_TTF = "application/x-font-ttf";
	private static final String APPLICATION_FONT_WOFF = "application/x-font-woff";
	private static final String TEXT_HTML = "text/html";
	private static final String TEXT_PLAIN = "text/plain";
	private static final String TEXT_CSS = "text/css";
	private static final String IMAGE_SVG = "image/svg+xml";
	private static final String IMAGE_PNG = "image/png";
	private static final String IMAGE_JPEG = "image/jpeg";
	static {
		TYPES.put("json", APPLICATION_JSON);
		TYPES.put("js", APPLICATION_JAVASCRIPT);
		TYPES.put("html", TEXT_HTML);
		TYPES.put("htm", TEXT_HTML);
		TYPES.put("txt", TEXT_PLAIN);
		TYPES.put("text", TEXT_PLAIN);
		TYPES.put("log", TEXT_PLAIN);
		TYPES.put("css", TEXT_CSS);
		TYPES.put("eot", APPLICATION_FONTOBJECT);
		TYPES.put("ttf", APPLICATION_FONT_TTF);
		TYPES.put("ttc", APPLICATION_FONT_TTF);
		TYPES.put("woff", APPLICATION_FONT_WOFF);
		TYPES.put("svg", IMAGE_SVG);
		TYPES.put("svgz", IMAGE_SVG);
		TYPES.put("png", IMAGE_PNG);
		TYPES.put("jpeg", IMAGE_JPEG);
		TYPES.put("jpg", IMAGE_JPEG);
		TYPES.put("jpe", IMAGE_JPEG);
	}

	public static String ofFile(String filename) {
		int dot_pos = filename.lastIndexOf("."); // period index

		if (dot_pos < 0) {
			return DEFAULT_TYPE;
		}
		
		String extension = filename.substring(dot_pos + 1);
        if (extension.length() == 0) {
        	 return DEFAULT_TYPE;
        }
        
        if(TYPES.containsKey(extension)) {
        	return TYPES.get(extension);
        } else {
        	return DEFAULT_TYPE;
        }
	}

}
