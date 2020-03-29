@streamApi @standalone
Feature: Http Input streamed service

Scenario: Basic Http Handle
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.StreamAPITest"
	Then I check that "#serviceFuture" is success
	And I post "/streamed" with body stream "#stream", content part "Hello ", length of 20 getting response "#response"
	Then I check that "#response" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I send "The end" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is success
	And I check that http response "#response" has body "Hello stream.The end"
	
Scenario: Basic Http handle error
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.StreamAPITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /streamed/error" with body "Hello!" getting "#response"
	And I check that "#response" has status code 500
