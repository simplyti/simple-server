package com.simplyti.service.injector;

import com.google.inject.ScopeAnnotation;
import org.apiguardian.api.API;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@ScopeAnnotation
@API(status = API.Status.STABLE)
public @interface ScenarioScoped {

}