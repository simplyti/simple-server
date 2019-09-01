package com.simplyti.service.builder.di.dagger;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import com.simplyti.service.ServerConfig;
import com.simplyti.service.channel.handler.FileServeHandler;
import com.simplyti.service.channel.handler.ServerHeadersHandler;
import com.simplyti.service.channel.handler.inits.FileServerHandlerInit;
import com.simplyti.service.channel.handler.inits.HandlerInit;
import com.simplyti.service.fileserver.DirectoryResolver;
import com.simplyti.service.fileserver.FileServe;
import com.simplyti.service.fileserver.FileServeConfiguration;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

@Module
public class FileServer {
	
	@Provides
	@Singleton
	@Nullable
	public FileServeConfiguration fileServeConfiguration(
			@Nullable @Named("fileServerPath") String path,
			@Nullable @Named("fileServerDirectory") String directory) {
		if(path!=null && directory!=null) {
			return new FileServeConfiguration(Pattern.compile("^"+path+"/(.*)$"),DirectoryResolver.literal(directory));
		} else {
			return null;
		}
	}

	@Provides
	@ElementsIntoSet
	public Set<HandlerInit> apiRequestHandlerInit( ServerHeadersHandler serverHeadersHandler,
			ServerConfig config,FileServe fileServer) {
		if(config.fileServer()!=null) {
			return Collections.singleton(new FileServerHandlerInit(new FileServeHandler(config, fileServer),serverHeadersHandler,config));
		}else {
			return Collections.emptySet();
		}
	}
	
	@Provides
	@Singleton
	public FileServe fileServer() {
		return new FileServe();
	}
	
}
