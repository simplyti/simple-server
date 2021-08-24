package com.simplyti.service.serializer.json;

import org.graalvm.collections.EconomicSet;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import com.dslplatform.json.Configuration;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.FeatureImpl.AfterRegistrationAccessImpl;

@AutomaticFeature
public class DslJsonGeneratedConfiguration implements Feature {
	
	private EconomicSet<Class<?>> dslConfigs = EconomicSet.create();
	
	@Override
	public void beforeAnalysis(final BeforeAnalysisAccess access) {
		dslConfigs.forEach(this::register);
	}
	
	private void register(Class<?> clazz) {
		try {
			RuntimeReflection.register(clazz);
			RuntimeReflection.register(clazz.getDeclaredConstructor());
		} catch (NoSuchMethodException | SecurityException e) { } 
	}
	
	@Override
    public void afterRegistration(AfterRegistrationAccess access) {
		((AfterRegistrationAccessImpl) access).getImageClassLoader().findSubclasses(Configuration.class, true)
			.stream()
			.filter(c->c.getSimpleName().startsWith("_"))
			.filter(c->c.getSimpleName().endsWith("_DslJsonConverter"))
			.forEach(dslConfigs::add);
	}
	
}
