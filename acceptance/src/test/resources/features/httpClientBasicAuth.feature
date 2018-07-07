Feature: Http Client basic auth

Scenario: Basic auth
	When I get "/basic-auth/pepe/supersecret" to port 8081 with basic auth "pepe" "supersecret" getting response "#response"
	Then I check that "#response" is success
	And I check that http response "#response" has body
	"""
	{
	  "authenticated": true, 
	  "user": "pepe"
	}
	
	"""
	
Scenario: Auth error
	When I get "/basic-auth/pepe/supersecret" to port 8081 getting response "#response"
	Then I check that "#response" is failure
	And I check that http error of "#response" contains status code 401
