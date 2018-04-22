Feature: Ssl serve

Scenario: Ssl listerner
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" using ssl port 8443 getting "#response"
	Then I check that "#response" is equals to "Hello!"

Scenario: Custom certificate provider
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule			| com.simplyti.service.CustomServerCertificateProviderModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" using ssl port 8443 getting "#response"
	Then I check that "#response" is equals to "Hello!"
	
Scenario: Custom certificate provider with server name indicator
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.APITest	|
		| withModule			| com.simplyti.service.CustomServerCertificateProviderModule |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" using ssl port 8443 with sni "example.com" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	And I check that server certificate has name "CN=example.com"
	
Scenario: I specify secured port
	When I try to start a service "#serviceFuture" with options getting error "#error":
		| option	 			| value |
		| withApi			| com.simplyti.service.APITest	|
		| securedPort		| 4443	|
	When I send a "GET /hello" using ssl port 4443 getting "#response"
	Then I check that "#response" is equals to "Hello!"