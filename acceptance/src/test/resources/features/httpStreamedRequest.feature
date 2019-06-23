Feature: Http Streamed service

Scenario: Basic Http Handle
	When I start a service "#serviceFuture" with API "com.simplyti.service.StreamAPITest"
	Then I check that "#serviceFuture" is success
	And I post "/streamed" with body stream "#stream", content part "Hello ", length of 20 getting response objects "#response"
	Then I check that stream "#stream" is not complete
	When I send "stream." to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is not complete
	When I send "The end" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that stream "#stream" is success
	And I check that response stream "#response" contains body "Hello stream.The end"