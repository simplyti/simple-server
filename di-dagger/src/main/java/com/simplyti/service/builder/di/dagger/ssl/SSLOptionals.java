package com.simplyti.service.builder.di.dagger.ssl;

import com.simplyti.service.ssl.DefaultServerCertificateProvider;
import com.simplyti.service.ssl.ServerCertificateProvider;

import dagger.BindsOptionalOf;
import dagger.Module;

@Module
public abstract class SSLOptionals {
	
	@BindsOptionalOf abstract DefaultServerCertificateProvider defaultServerCertificateProvider();
	@BindsOptionalOf abstract ServerCertificateProvider serverCertificateProvider();

}
