@streamApi @standalone
Feature: Http chunked response body api

Scenario: Send chunked response while stream request processing
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.StreamAPITest"
	Then I check that "#serviceFuture" is success
	When I post "/echo/chunked" with body stream "#stream" handling objects "#objects" and getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream 1!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that object list "#objects" has size 1
	And I check that object 0 in list "#objects" is equals to "Hello stream 1!"
	When I send "Hello stream 2!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that object list "#objects" has size 2
	And I check that object 1 in list "#objects" is equals to "Hello stream 2!"
	Then I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	
Scenario: Basic Http handle error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.StreamAPITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /streamed/error" with body "Hello!" getting "#response"
	And I check that "#response" has status code 500
