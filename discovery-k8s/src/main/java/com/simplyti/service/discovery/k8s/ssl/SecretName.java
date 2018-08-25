package com.simplyti.service.discovery.k8s.ssl;

public class SecretName {
	
	private final String name;
	private int count;
	
	public SecretName(String name) {
		this.name=name;
		this.count = 1;
	}

	public String name() {
		return name;
	}

	public SecretName retain() {
		count++;
		return this;
	}
	
	public SecretName release() {
		count--;
		return this;
	}

	public int count() {
		return count;
	}

	

}
