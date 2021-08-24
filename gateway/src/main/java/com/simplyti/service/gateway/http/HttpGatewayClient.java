package com.simplyti.service.gateway.http;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface HttpGatewayClient {

}
