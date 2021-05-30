@standalone @clients @httpClient @httpClientStreamResponse
Feature: Http Client streamed response handle

Background: Start Server
  Given I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIChunkExample"
	Then I check that "#serviceFuture" is success

Scenario: Http request streamed response handled
	When I get "/chunked?count=8" handling objects "#objects" and getting result "#result"
	Then I check that "#result" is success
	And I check that object list "#objects" has size 8
	And I check that object 0 in list "#objects" is equals to "Hello 1"
	And I check that object 1 in list "#objects" is equals to "Hello 2"
	And I check that object 2 in list "#objects" is equals to "Hello 3"
	And I check that object 3 in list "#objects" is equals to "Hello 4"
	And I check that object 4 in list "#objects" is equals to "Hello 5"
	And I check that object 5 in list "#objects" is equals to "Hello 6"
	And I check that object 6 in list "#objects" is equals to "Hello 7"
	And I check that object 7 in list "#objects" is equals to "Hello 8"
	And I check that http client has 1 iddle connection
	When I get "/chunked?count=1" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello 1"
	And I check that http client has 1 iddle connection

Scenario: Http request with custom channel handler
	When I get "/chunked?count=8&json" handling json objects "#objects" and getting result "#result"
	Then I check that "#result" is success
	And I check that object list "#objects" has size 8
	And I check that json object 0 in list "#objects" has property "message" equals to "Hello 1"
	And I check that json object 1 in list "#objects" has property "message" equals to "Hello 2"
	And I check that json object 2 in list "#objects" has property "message" equals to "Hello 3"
	And I check that json object 3 in list "#objects" has property "message" equals to "Hello 4"
	And I check that json object 4 in list "#objects" has property "message" equals to "Hello 5"
	And I check that json object 5 in list "#objects" has property "message" equals to "Hello 6"
	And I check that json object 6 in list "#objects" has property "message" equals to "Hello 7"
	And I check that json object 7 in list "#objects" has property "message" equals to "Hello 8"
	And I check that http client has 1 iddle connection
	When I get "/chunked?count=1&json" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body '{"message":"Hello 1"}'
	And I check that http client has 1 iddle connection
	
