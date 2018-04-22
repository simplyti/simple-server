Feature: JAX RS Interface

Scenario: Get Method
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	
Scenario: Post Method
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /jaxrs/echo" with body "Hello echo!" getting "#response"
	Then I check that "#response" is equals to "Hello echo!"

Scenario: Path param 
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/pathparams/arantxa" getting "#response"
	Then I check that "#response" is equals to "arantxa"
	
Scenario: Query param types
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams?string=Pablo&short=1&int=2&double=2.5&long=3&float=3.5" getting "#response"
	Then I check that "#response" is equals to "Pablo|1|2|2.5|3|3.5"

Scenario: Query param primitives types
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/primitives?string=Pablo&short=1&int=2&double=2.5&long=3&float=3.5" getting "#response"
	Then I check that "#response" is equals to "Pablo|1|2|2.5|3|3.5"

Scenario: Query list
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/list?string=Pablo&string=Arantxa" getting "#response"
	Then I check that "#response" is equals to "Pablo,Arantxa"

Scenario: Query coma separated list
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/list?string=Pablo,Arantxa" getting "#response"
	Then I check that "#response" is equals to "Pablo,Arantxa"
	
Scenario: Query param default value
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/default" getting "#response"
	Then I check that "#response" is equals to "Hello Default!"
	
Scenario: Method throw exception
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/throwexception" getting "#response"
	And I check that "#response" has status code 500
	
Scenario: Blocking method
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/blocking" getting "#response"
	Then I check that "#response" is equals to "Hello!"
	
Scenario: Blocking method Method throw exception
	When I start a service "#serviceFuture" with API "com.simplyti.service.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/blocking/throwexception" getting "#response"
	And I check that "#response" has status code 500
	
	
