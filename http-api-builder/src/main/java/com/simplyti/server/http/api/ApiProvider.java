package com.simplyti.server.http.api;

import com.simplyti.server.http.api.builder.ApiBuilder;

public interface ApiProvider {

	void build(ApiBuilder builder);

}
