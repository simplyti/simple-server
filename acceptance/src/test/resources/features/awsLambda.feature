Feature: AWS Lambda entry

Scenario: Basic AWS Lambda invoke
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "GET" and path "/hello" getting response "#response"
	Then I check that lambda result "#response" has status code 200
	And I check that lambda result "#response" has body "Hello!"
	
	
Scenario: Lambda channel must be reused
	When I create the aws lambda "#lambda" service "com.simplyti.service.aws.APITestLamda"
	And I invoke the aws lambda "#lambda" with http method "GET" and path "/hello" getting response "#response"
	Then I check that lambda result "#response" has status code 200
	When I invoke the aws lambda "#lambda" with http method "GET" and path "/hello" getting response "#response"
	Then I check that lambda result "#response" has status code 200