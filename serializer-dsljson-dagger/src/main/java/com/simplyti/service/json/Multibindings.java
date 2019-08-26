package com.simplyti.service.json;

import java.util.Set;

import com.dslplatform.json.Configuration;

import dagger.Module;
import dagger.multibindings.Multibinds;

@Module
public interface Multibindings {
	
	@Multibinds abstract Set<Configuration> configurations();

}
