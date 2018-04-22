Feature: Http Client

Scenario: Get request
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Post request
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo" with body "Hey!" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hey!"

Scenario: Get request https
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get url "https://localhost:8443/hello" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello!"
	
Scenario: Http client error
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/responsecode/401" getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
	
Scenario: Connection error
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I get "/echo" to port 9090 getting response "#response"
	Then I check that "#response" is failure
	
Scenario Outline: Get external endpoint socks proxy
	Given "<type>" proxy "<address>" as "#proxy"
	When I get url "http://httpbin:8080/status/204" through proxy "#proxy" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 204
	And I check that http response "#response" has body ""
	Examples:
	| type 		| address 			|
	| SOCKS5		| 127.0.0.1:1080		|
	| HTTP		| 127.0.0.1:3128		|

Scenario: Get http objects
	When I get url "http://127.0.0.1:8081/stream/5" getting http objects "#objects"
	Then I check that http objects "#objects" contains 7 items
	
Scenario: Get http stream
	When I get url "http://127.0.0.1:8081/stream/5" getting stream "#stream"
	Then I check that stream "#stream" contains 5 items
