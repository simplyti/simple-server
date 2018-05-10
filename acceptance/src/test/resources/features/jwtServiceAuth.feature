Feature: JWT Service Authorization

Scenario: JWT Service auth
	Given a JWT sign key "#key"
	And a valid JWT token "#token" signed with alg "HS512" with key "#key"
	And a JWT auth module "#module" with key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with jwt token "#token" getting "#response"
	And I check that "#response" has status code 200
	Then I check that "#response" is equals to "Hello!"
	
Scenario: JWT Service auth, no auth endpoint
	Given a JWT sign key "#key"
	And a JWT auth module "#module" with key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /noauth/hello" getting "#response"
	And I check that "#response" has status code 200
	Then I check that "#response" is equals to "Hello!"
	
Scenario: JWT Service auth, no Auth header present
	Given a JWT sign key "#key"
	And a valid JWT token "#token" signed with alg "HS512" with key "#key"
	And a JWT auth module "#module" with key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 401
	Then I check that "#response" is equals to ""
	
Scenario: JWT Service auth, bad signature
	Given a JWT sign key "#key"
	And a JWT auth module "#module" with key "#key"
	And a invalid JWT token "#token" signed with alg "HS512"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with jwt token "#token" getting "#response"
	And I check that "#response" has status code 401
	Then I check that "#response" is equals to ""
	
Scenario: JWT Service auth, bad token
	Given a JWT sign key "#key"
	And a JWT auth module "#module" with key "#key"
	And a malformed JWT token "#token"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with jwt token "#token" getting "#response"
	And I check that "#response" has status code 401
	Then I check that "#response" is equals to ""
	
Scenario: JWT Service auth, bad prefix
	Given a JWT sign key "#key"
	And a JWT auth module "#module" with key "#key"
	And a malformed JWT token "#token"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with authorizarion header "Unknownprefix abcd1234" getting "#response"
	And I check that "#response" has status code 401
	Then I check that "#response" is equals to ""

Scenario: JWT Service auth, no Auth header present, jaxrs
	Given a JWT sign key "#key"
	And a valid JWT token "#token" signed with alg "HS512" with key "#key"
	And a JWT auth module "#module" with key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.JWTTestApi |
		| withModule			| #module |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/hello" getting "#response"
	And I check that "#response" has status code 401
	Then I check that "#response" is equals to ""