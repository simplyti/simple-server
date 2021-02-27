@standalone  @clients @httpClient @httpClientFilter
Feature: Http Client filter

Scenario: Full request filter
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an http client "#client" with pool size 1 and filter "com.simplyti.service.examples.filter.AddHTTPHeadereFilter"
	When I get "/header/x-filter-name" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Pepe"
	When I get "/header/x-filter-name-delay" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Delayed: Pepe"
	When I get "/header/x-filter-name-resolve" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Resolved: Pepe"
	When I get "/header/x-filter-name-resolve-delay" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Resolved Delayed: Pepe"
	When I get "/header/x-filter-name" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Pepe"
	
Scenario: Full request filter with chunked request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an http client "#client" with pool size 1 and filter "com.simplyti.service.examples.filter.HTTPHeadereFilter"
	When I post "/header/echo" using client "#client" with body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	When I post "/header/echo" using client "#client" with header "x-req-filter" "delay" body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream! delay"
	When I post "/header/echo" using client "#client" with header "x-req-filter" "resolve" body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	And I check that http response "#response" has body "Hello Resolved!"
	When I post "/header/echo" using client "#client" with header "x-req-filter" "resolvedelay" body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	And I check that http response "#response" has body "Hello Resolved Delayed!"
	When I post "/header/echo" using client "#client" with body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	
Scenario: Full request filter per request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an http client "#client" with pool size 1
	When I get "/header/x-filter-name" with filter "com.simplyti.service.examples.filter.AddHTTPHeadereFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Pepe"
	When I get "/header/x-filter-name-delay" with filter "com.simplyti.service.examples.filter.AddHTTPHeadereFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Delayed: Pepe"
	When I get "/header/x-filter-name-resolve" with filter "com.simplyti.service.examples.filter.AddHTTPHeadereFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Resolved: Pepe"
	When I get "/header/x-filter-name-resolve-delay" with filter "com.simplyti.service.examples.filter.AddHTTPHeadereFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Resolved Delayed: Pepe"
	When I get "/header/x-filter-name" with filter "com.simplyti.service.examples.filter.AddHTTPHeadereFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello Pepe"
	
Scenario: Full request filter with chunked request
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I create an http client "#client" with pool size 1
	When I post "/header/echo" with filter "com.simplyti.service.examples.filter.HTTPHeadereFilter" using client "#client" with body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
	When I post "/header/echo" with filter "com.simplyti.service.examples.filter.HTTPHeadereFilter" using client "#client" with header "x-req-filter" "delay" body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream! delay"
	When I post "/header/echo" with filter "com.simplyti.service.examples.filter.HTTPHeadereFilter" using client "#client" with header "x-req-filter" "resolve" body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	And I check that http response "#response" has body "Hello Resolved!"
	When I post "/header/echo" with filter "com.simplyti.service.examples.filter.HTTPHeadereFilter" using client "#client" with header "x-req-filter" "resolvedelay" body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	And I check that http response "#response" has body "Hello Resolved Delayed!"
	When I post "/header/echo" with filter "com.simplyti.service.examples.filter.HTTPHeadereFilter" using client "#client" with body stream "#stream" getting response "#response"
	Then I check that "#response" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#response" is not complete
	When I close request stream "#stream"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Hello stream!"
