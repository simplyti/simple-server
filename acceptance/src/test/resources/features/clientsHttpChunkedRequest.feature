@standalone @clients @httpClient @httpClientChunkedRequest
Feature: Http Client sending chunked request

Scenario: Send chunked body data
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success
	When I post "/echo" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	And I check that http client has 1 iddle connection
	When I post "/echo" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Bye stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Bye stream!"
	And I check that http client has 1 iddle connection
	
Scenario: Send chunked body data and handle response in an streamed way
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIChunkExample"
	Then I check that "#serviceFuture" is success
	When I post "/echo/chunked" with chunked body stream "#stream" handling objects "#objects" and getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream 1!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that object list "#objects" has size 1
	And I check that object 0 in list "#objects" is equals to "Hello stream 1!"
	When I send "Hello stream 2!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that object list "#objects" has size 2
	And I check that object 1 in list "#objects" is equals to "Hello stream 2!"
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
