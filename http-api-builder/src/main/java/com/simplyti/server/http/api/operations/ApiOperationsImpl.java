package com.simplyti.server.http.api.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import com.simplyti.server.http.api.context.ApiContext;

public class ApiOperationsImpl implements ApiOperations {
	
	private static final Comparator<ApiOperation<? extends ApiContext>> COMPARATOR = new Comparator<ApiOperation<? extends ApiContext>>() {

		@Override
		public int compare(com.simplyti.server.http.api.operations.ApiOperation<? extends ApiContext> o1,
				com.simplyti.server.http.api.operations.ApiOperation<? extends ApiContext> o2) {
			return o2.pattern().literalCharsCount() - o1.pattern().literalCharsCount();
		}};
	
	private final List<ApiOperation<? extends ApiContext>> operations;
	

	@Inject
	public ApiOperationsImpl() {
		this.operations = new ArrayList<>();
	}

	@Override
	public void add(ApiOperation<? extends ApiContext> operation) {
		this.operations.add(operation);
	}

	@Override
	public List<ApiOperation<? extends ApiContext>> getAll() {
		return operations;
	}

	@Override
	public void sort() {
		Collections.sort(this.operations,COMPARATOR);
	}

}
