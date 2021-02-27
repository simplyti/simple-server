@standalone  @clients @httpClient @httpClientServerSentEvents
Feature: Http Client Server sent events

Scenario: Get Server sent event stream
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.SSEApi"
	Then I check that "#serviceFuture" is success
	And I get "/sse" getting sse stream "#sse" and result "#result"
	Then I check that "#result" is finished
	And I check that stream "#sse" contains 2 items
	And I check that http client has 0 iddle connection
	And I check that http client has 0 active connection
	