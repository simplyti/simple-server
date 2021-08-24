package com.simplyti.service.builder.di.guice;

import com.simplyti.service.transport.Listener;

public interface Listenable {

	ServiceBuilder listen(Listener listener);

}
