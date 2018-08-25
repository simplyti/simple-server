Feature: OpenId

Scenario: Request without JWT gets 401 status
	Given a selfsigned certificate "#certificate"
    And a JWT sign key "#key" from self signed ccertificate "#certificate"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 401
	
Scenario: Request with valid JWT gets 200 status
	Given a JWT sign key "#key"
	And a valid JWT token "#token" signed with alg "HS512" with key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with jwt token "#token" getting "#response"
	And I check that "#response" has status code 200
	Then I check that "#response" is equals to "Hello OIDC!"
	
Scenario: Request with an invalid JWT gets 401 status
	Given a JWT sign key "#key"
	And an invalid JWT token "#token" signed with alg "HS512"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with jwt token "#token" getting "#response"
	And I check that "#response" has status code 401
	
Scenario: Request with a malformed JWT gets 401 status
	Given a JWT sign key "#key"
	And a malformed JWT token "#token"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" with jwt token "#token" getting "#response"
	And I check that "#response" has status code 401
	
Scenario: Request with no auth required gets 200
    Given a JWT sign key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /noauth/hello" getting "#response"
	And I check that "#response" has status code 200
	Then I check that "#response" is equals to "Hello!"
	
Scenario: Service should redirect to openid provider when configuration is provided
    Given a JWT sign key "#key"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key,https://localhost:7443/auth,https://example.org/callback,myClientId) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" has status code 302 with location starting with "https://localhost:7443/auth"
	And I check that "#response" redirect location contains params:
		| redirect_uri		| https://example.org/callback	|
		| response_type		| code | 
		| client_id			| myClientId	|
		| scope				| openid email profile groups		|
		| approval_prompt	| force 								|
		| access_type		| offline							|
		
Scenario: Service performs full openid flow when all parameters are provided
    Given a selfsigned certificate "#certificate"
    And a JWT sign key "#key" from self signed ccertificate "#certificate"
    Given an openid provider listening in port 7443 with sign certificate "#certificate" and token endpoint "/token"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(#key,https://localhost:7443/auth,https://localhost:7443/token,/_api/callback,myClientId,myClientSecret,myCipherKey) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	Then I check that "#response" has status code 302 with location starting with "https://localhost:7443/auth"
	And I check that "#response" redirect location contains params:
		| redirect_uri		| https://localhost/_api/callback	|
		| response_type		| code | 
		| client_id			| myClientId	|
		| scope				| openid email profile groups		|
		| approval_prompt	| force 								|
		| access_type		| offline							|
	When I send a "GET /_api/callback" following auth redirect of "#response" getting "#response"
	Then I check that "#response" has status code 302 
	And I check that "#response" has location header "https://localhost/hello"
	And I check that "#response" has cookie "JWT-SESSION"
	When I send a "GET /hello" with cookies from response "#response" getting "#response"
	Then I check that "#response" has status code 200
	And I check that "#response" is equals to "Hello OIDC!"
	
Scenario: Wellknown OpenId provider configuration
    Given a selfsigned certificate "#certificate"
    And a JWT sign key "#key" from self signed ccertificate "#certificate"
    And an openid provider listening in port 7443 with sign certificate "#certificate", authorization endpoint "/authorize" and token endpoint "/token"
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(https://localhost:7443,/_api/callback,myClientId,myClientSecret,myCipherKey) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response" with status code 302 eventually
	Then I check that "#response" has status code 302 with location starting with "https://localhost:7443/authorize"
	And I check that "#response" redirect location contains params:
		| redirect_uri		| https://localhost/_api/callback	|
		| response_type		| code | 
		| client_id			| myClientId	|
		| scope				| openid email profile groups		|
		| approval_prompt	| force 								|
		| access_type		| offline							|
	When I send a "GET /_api/callback" following auth redirect of "#response" getting "#response"
	Then I check that "#response" has status code 302 
	And I check that "#response" has location header "https://localhost/hello"
	And I check that "#response" has cookie "JWT-SESSION"
	When I send a "GET /hello" with cookies from response "#response" getting "#response"
	Then I check that "#response" has status code 200
	
Scenario: Wellknown OpenId provider configuration with delay
    Given a selfsigned certificate "#certificate"
    And a JWT sign key "#key" from self signed ccertificate "#certificate"
    And an openid provider listening in port 7443 with sign certificate "#certificate", authorization endpoint "/authorize" and well-known service with 1000ms of delay
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(https://localhost:7443,/_api/callback,myClientId,myClientSecret,myCipherKey) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response"
	And I check that "#response" has status code 401
	
Scenario: Wellknown OpenId provider configuration with jwks service delay
    Given a selfsigned certificate "#certificate"
    And a JWT sign key "#key" from self signed ccertificate "#certificate"
    And an openid provider listening in port 7443 with sign certificate "#certificate", authorization endpoint "/authorize", token endpoint "/token" and jwks service with 1000ms of delay
	When I start a service "#serviceFuture" with options:
		| option	 			| value |
		| withApi			| com.simplyti.service.OidcTestApi |
		| withModule			| com.simplyti.service.security.oidc.OpenIdModule(https://localhost:7443,/_api/callback,myClientId,myClientSecret,myCipherKey) |
		| withLog4J2Logger	|		|
	Then I check that "#serviceFuture" is success
	When I send a "GET /hello" getting "#response" with status code 302 eventually
	Then I check that "#response" has status code 302 with location starting with "https://localhost:7443/authorize"
	And I check that "#response" redirect location contains params:
		| redirect_uri		| https://localhost/_api/callback	|
		| response_type		| code | 
		| client_id			| myClientId	|
		| scope				| openid email profile groups		|
		| approval_prompt	| force 								|
		| access_type		| offline							|
	When I send a "GET /_api/callback" following auth redirect of "#response" getting "#response"
	Then I check that "#response" has status code 302 
	And I check that "#response" has location header "https://localhost/hello"
	And I check that "#response" has cookie "JWT-SESSION"
	When I send a "GET /hello" with cookies from response "#response" getting "#response"
	Then I check that "#response" has status code 503
