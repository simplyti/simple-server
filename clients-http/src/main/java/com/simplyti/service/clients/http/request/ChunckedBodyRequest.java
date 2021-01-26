package com.simplyti.service.clients.http.request;

import com.simplyti.util.concurrent.Future;

public interface ChunckedBodyRequest {

	Future<Void> send(String data);

	Future<Void> end();

}
