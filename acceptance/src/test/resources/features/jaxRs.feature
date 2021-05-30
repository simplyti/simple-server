@jaxrs @standalone
Feature: JAX RS Interface

Scenario: Get Method
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"

Scenario: Post Method
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "POST /jaxrs/echo" with body '{"message":"Hello echo!"}' getting "#response"
	Then I check that "#response" is equals to '{"message":"Hello echo!"}'

Scenario: Path param 
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/pathparams/arantxa" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "arantxa"
	
Scenario: Query param types
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams?string=Pablo&short=1&int=2&double=2.5&long=3&float=3.5" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Pablo|1|2|2.5|3|3.5"

Scenario: Query param primitives types
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/primitives?string=Pablo&short=1&int=2&double=2.5&long=3&float=3.5" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Pablo|1|2|2.5|3|3.5"

Scenario: Query list
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/list?string=Pablo&string=Arantxa" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Pablo,Arantxa"

Scenario: Query coma separated list
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/list?string=Pablo,Arantxa" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Pablo,Arantxa"
	
Scenario: Query param default value
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/queryparams/default" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello Default!"
	
Scenario: Method throw exception
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/throwexception" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Failure service
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/failure?msg=Error!" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Blocking method
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/blocking" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 200
	And I check that http response "#response" has body "Hello!"
	
Scenario: Blocking method Method throw exception
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/blocking/throwexception" getting "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has status code 500
	And I check that http response "#response" has body ""
	
Scenario: Header param 
	When I start a service "#serviceFuture" with API "com.simplyti.service.examples.api.APITest"
	Then I check that "#serviceFuture" is success
	When I send a "GET /jaxrs/headerparam" with header "x-my-header" with value "Hello header" getting "#response"
	Then I check that "#response" is equals to "Hello header"
