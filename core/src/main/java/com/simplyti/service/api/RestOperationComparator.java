package com.simplyti.service.api;

import java.util.Comparator;

public class RestOperationComparator implements Comparator<ApiOperation<?,?>> {

	public static final Comparator<ApiOperation<?,?>> INSTANCE = new RestOperationComparator();

	@Override
	public int compare(ApiOperation<?, ?> o1, ApiOperation<?, ?> o2) {
		return o2.literalChars() - o1.literalChars();
	}


}

