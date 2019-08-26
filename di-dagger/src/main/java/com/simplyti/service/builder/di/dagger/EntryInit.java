package com.simplyti.service.builder.di.dagger;

import com.simplyti.service.channel.EntryChannelInit;

import dagger.BindsOptionalOf;
import dagger.Module;

@Module
public interface EntryInit {
	
	@BindsOptionalOf EntryChannelInit entryChannelInit();
	
}
