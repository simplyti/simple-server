@standalone @clients @httpClient @httpClientAuth
Feature: Http Client auth

Background: Start Server
  Given I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.ClientHttpAuth"
	Then I check that "#serviceFuture" is success

Scenario: Http client with basic auth
	When I create an http client "#client" with basic auth "pepe" "supersecret"
	And I get "/basic-auth/pepe/supersecret" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello pepe"
	When I get "/basic-auth/pepe/supersecret" with basic auth "pepe" "badsecret" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	
Scenario: Http client with bearer auth
	When I create an http client "#client" with bearer auth "eyJuYW1lIjoicGVwZSJ9"
	And I post "/bearer-auth/pepe" using client "#client" with body "Bye" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Bye pepe"
	When I post "/bearer-auth/pepe" with body "Bye" and bearer auth "eyJuYW1lIjoib3RoZXIifQ==" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	
Scenario: Basic auth request
	When I get "/basic-auth/pepe/supersecret" with basic auth "pepe" "supersecret" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello pepe"
	When I post "/basic-auth/pepe/supersecret" with body "Bye" and basic auth "pepe" "supersecret" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Bye pepe"
	
Scenario: Bearer auth request
	When I get "/bearer-auth/pepe" with bearer auth "eyJuYW1lIjoicGVwZSJ9" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello pepe"
	When I post "/bearer-auth/pepe" with body "Bye" and bearer auth "eyJuYW1lIjoicGVwZSJ9" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Bye pepe"
