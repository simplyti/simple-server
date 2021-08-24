package com.simplyti.server.http.api.operations;


import java.util.List;

import com.simplyti.server.http.api.context.ApiContext;

public interface ApiOperations {

	void add(ApiOperation<? extends ApiContext> operation);

	List<ApiOperation<? extends ApiContext>> getAll();

	void sort();

}
