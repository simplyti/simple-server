@standalone @clients @httpClient @httpClientServerSentEvents
Feature: Http Client Server sent events

Scenario: Get Server sent event stream
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APISSEExample"
	Then I check that "#serviceFuture" is success
	And I get "/sse?count=6" getting sse stream "#sse" and result "#result"
	Then I check that "#result" is finished
	And I check that stream "#sse" contains 6 items
	And I check that sse stream "#sse" has item 0 with data "Hello 1" 
	And I check that sse stream "#sse" has item 1 with data "Hello 2" 
	And I check that sse stream "#sse" has item 2 with data "Hello 3" 
	And I check that sse stream "#sse" has item 3 with data "Hello 4" 
	And I check that sse stream "#sse" has item 4 with data "Hello 5" 
	And I check that sse stream "#sse" has item 5 with data "Hello 6" 
	And I check that http client has 0 iddle connection
	And I check that http client has 0 active connection
	