@standalone  @clients @httpClient @httpClientFilter
Feature: Http Client

Scenario: Full request filter
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I crate http client filter "#filter" of class "com.simplyti.service.examples.filter.AddHTTPHeadereFilter"
	And I get "/header/x-filter-name" with filter "#filter" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Pepe"
	
Scenario: Full request filter delay
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I crate http client filter "#filter" of class "com.simplyti.service.examples.filter.AddHTTPHeadereFilter"
	And I get "/header/x-filter-name-delay" with filter "#filter" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Delayed: Pepe"