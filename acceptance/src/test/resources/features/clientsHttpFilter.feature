@standalone @clients @httpClient @httpClientFilter
Feature: Http Client filter

Background: Start Server
  Given I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APIExample"
	Then I check that "#serviceFuture" is success

Scenario: Full request filter
	When I create an http client "#client" with pool size 1 and filter "com.simplyti.service.examples.filter.QueryToHeaderFilter"
	And I get "/get/headers-to-json?prop1=value1&prop2=value2" using client "#client" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	When I get "/get/headers-to-json?prop1=value1&prop2=value2&delay=500" using client "#client" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "delayed" equals to "true"
	When I get "/get/headers-to-json?resolv" using client "#client" getting response "#response"
	Then I check that "#any" is success
	And I check that http response "#response" has body "Filter resolved!"
	When I get "/get/headers-to-json?resolv&delay=500" using client "#client" getting response "#response"
	Then I check that "#any" is success
	And I check that http response "#response" has body "Filter delayed resolved!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2" using client "#client" with body "Hello!" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "body" equals to "Hello!"
	
Scenario: Full request filter with chunked body
	When I create an http client "#client" with pool size 1 and filter "com.simplyti.service.examples.filter.QueryToHeaderFilter"
	And I post "/post/headers-to-json?prop1=value1&prop2=value2" using client "#client" with chunked body stream "#stream" getting json response "#any"
	Then I check that "#any" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#any" is not complete
	When I close request stream "#stream"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "body" equals to "Hello stream!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2&delay=500" using client "#client" with chunked body stream "#stream" getting json response "#any"
	Then I check that "#any" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#any" is not complete
	When I close request stream "#stream"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "delayed" equals to "true"
	And I check that json "#any" has property "body" equals to "Hello stream!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2&resolv" using client "#client" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that http response "#response" has body "Filter resolved!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2&resolv&delay=500" using client "#client" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that http response "#response" has body "Filter delayed resolved!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2" using client "#client" with chunked body stream "#stream" getting json response "#any"
	Then I check that "#any" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#any" is not complete
	When I close request stream "#stream"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "body" equals to "Hello stream!"
	
Scenario: Full request filter per request
	When I create an http client "#client" with pool size 1
	And I get "/get/headers-to-json?prop1=value1&prop2=value2" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	When I get "/get/headers-to-json?prop1=value1&prop2=value2&delay=500" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "delayed" equals to "true"
	When I get "/get/headers-to-json?resolv" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Filter resolved!"
	When I get "/get/headers-to-json?resolv&delay=500" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body "Filter delayed resolved!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" with body "Hello!" getting json response "#any"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "body" equals to "Hello!"
	
Scenario: Full request filter with chunked body per request
	When I create an http client "#client" with pool size 1
	And I post "/post/headers-to-json?prop1=value1&prop2=value2" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" with chunked body stream "#stream" getting json response "#any"
	Then I check that "#any" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#any" is not complete
	When I close request stream "#stream"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "body" equals to "Hello stream!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2&delay=500" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" with chunked body stream "#stream" getting json response "#any"
	Then I check that "#any" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#any" is not complete
	When I close request stream "#stream"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "delayed" equals to "true"
	And I check that json "#any" has property "body" equals to "Hello stream!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2&resolv" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that http response "#response" has body "Filter resolved!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2&resolv&delay=500" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" with chunked body stream "#stream" getting response "#response"
	Then I check that "#response" is success
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	When I close request stream "#stream"
	Then I check that http response "#response" has body "Filter delayed resolved!"
	When I post "/post/headers-to-json?prop1=value1&prop2=value2" with filter "com.simplyti.service.examples.filter.QueryToHeaderFilter" using client "#client" with chunked body stream "#stream" getting json response "#any"
	Then I check that "#any" is not complete
	When I send "Hello stream!" to stream "#stream" getting result "#writeresult"
	Then I check that "#writeresult" is success
	And I check that "#any" is not complete
	When I close request stream "#stream"
	Then I check that "#any" is success
	And I check that json "#any" has property "prop1" equals to "value1"
	And I check that json "#any" has property "prop2" equals to "value2"
	And I check that json "#any" has property "body" equals to "Hello stream!"
