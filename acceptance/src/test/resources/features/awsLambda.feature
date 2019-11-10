@standalone
Feature: AWS Lambda entry

Scenario: Basic AWS Lambda invoke
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "GET" and path "/hello" getting response "#response"
	Then I check that lambda result "#response" has status code 200
	And I check that lambda result "#response" has body "Hello!"
	
Scenario: Basic AWS Lambda not found
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "GET" and path "/notfound" getting response "#response"
	Then I check that lambda result "#response" has status code 404
	
Scenario: Basic AWS Lambda with empty query params
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "GET", path "/hello" and empty query parameters getting response "#response"
	Then I check that lambda result "#response" has status code 200
	
Scenario: Lambda channel must be reused
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "GET" and path "/hello" getting response "#response"
	Then I check that lambda result "#response" has status code 200
	When I invoke the aws lambda "#lambda" with http method "GET" and path "/hello" getting response "#response"
	Then I check that lambda result "#response" has status code 200
	
Scenario: Lambda channel with query params
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "POST", path "/echo/delay", query parameters "millis=100" and body "Hello!" getting response "#response"
	Then I check that lambda result "#response" has status code 200
	And I check that lambda result "#response" has body "Hello!"