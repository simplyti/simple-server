package com.simplyti.service.ssl;

import java.security.Provider;

@SuppressWarnings("serial")
public class IoCSecurityProvider extends Provider {
	
	private final static String ALGORITHM = "ioc";
    private final static Double VERSION = 1.0;
    private final static String INFO = "IoC Key Manager";

	public IoCSecurityProvider() {
		 super(ALGORITHM, VERSION, INFO);
	}

}
