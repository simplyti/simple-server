package com.simplyti.service.builder.di.dagger;


import com.simplyti.service.builder.di.dagger.nativeio.NativeIOModule;
import com.simplyti.service.builder.di.dagger.ssl.SSLModule;

import dagger.Module;

@Module(includes= { BaseServiceModule.class, NativeIOModule.class, SSLModule.class })
public class ServiceModule {}