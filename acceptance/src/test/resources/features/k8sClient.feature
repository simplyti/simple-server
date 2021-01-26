@k8sClients
Feature: Kubernetes client

Background:
	Given a namespace "acceptance"

Scenario: Endpoints creation and delete
	When I create an endpoint in namespace "acceptance" with name "test" and address "10.0.0.1:80"
	Then I check that exist an endpoint "#endpoint" with name "test" in namespace "acceptance"
	And I check that endpoint "#endpoint" contains a subset with addresses "10.0.0.1" and ports "80"
	When I delete endpoint "#endpoint"
	Then I check that doesnt exist an endpoint "test" in namespace "acceptance"
	
Scenario:  Update endpoint
	When I create an endpoint in namespace "acceptance" with name "test" and address "10.0.0.1:80"
	Then I check that exist an endpoint "#endpoint" with name "test" in namespace "acceptance"
	And I check that endpoint "#endpoint" contains a subset with addresses "10.0.0.1" and ports "80"
	When I update endpoint "#endpoint" setting address "10.0.0.2:80"
	Then I check that endpoint "#endpoint" contains a subset with addresses "10.0.0.2" and ports "80"
	
Scenario: Create service and delete
	When I create a service in namespace "acceptance" with name "test" with port 80 to target 8080
	Then I check that exist a service "#service" with name "test" in namespace "acceptance"
	And I check that service "#service" is listening on port 80 to target 8080
	When I delete service "#service"
	Then I check that doesnt exist a aservice "test" in namespace "acceptance"
	
Scenario: Update service
	When I create a service in namespace "acceptance" with name "test" with port 80 to target 8080
	Then I check that exist a service "#service" with name "test" in namespace "acceptance"
	And I check that service "#service" is listening on port 80 to target 8080
	When I update service "#service" setting port 9090 to target 8080
	Then I check that service "#service" is listening on port 9090 to target 8080
	
Scenario: Create ingress and delete
	When I create an ingress in namespace "acceptance" with name "test", path "/test" and backend service "theservice:80"
	Then I check that exist an ingress "#ingress" with name "test" in namespace "acceptance"
	When I delete ingress "#ingress"
	Then I check that doesnt exist an ingress "test" in namespace "acceptance"
	
Scenario: Update ingress
	When I create an ingress in namespace "acceptance" with name "test", path "/test" and backend service "theservice:80"
	Then I check that exist an ingress "#ingress" with name "test" in namespace "acceptance"
	And I check that ingress "#ingress" contains a path "/test" with backend service "theservice:80"
	When I update ingress "#ingress" adding path "/other" with backend service "otherservice:80"
	Then I check that ingress "#ingress" contains 2 rules
	
Scenario: Create secret and delete
	When I create a secret in namespace "acceptance" with name "test" and next data:
	| mysecretey | supersecret |
	Then I check that exist a secret "#secret" with name "test" in namespace "acceptance"
	When I delete secret "#secret"
	Then I check that doesnt exist a secret "test" in namespace "acceptance"
	
Scenario: Watch operation
	When I watch services event getting "#events" and an observable "#observable"
	Then I check that events list "#events" is empty
	When I create a service in namespace "acceptance" with name "test" with port 80 to target 8080
	Then I check that events list "#events" contains 1 event
	And I check that events list "#events" contains a "ADDED" event
	When I delete service "test" in namespace "acceptance"
	Then I check that events list "#events" contains 2 event
	And I check that events list "#events" contains a "DELETED" event in index 1
	When I stop watchin the observable "#observable"
	When I create a service in namespace "acceptance" with name "test" with port 80 to target 8080
	Then I check that events list "#events" still containing 2 event
	
Scenario: Create a service account
	When I create a service account in namespace "acceptance" with name "test"
	Then I check that exist a service account "#serviceaccount" with name "test" in namespace "acceptance"
	When I delete service account "#serviceaccount"
	Then I check that doesnt exist a service account "test" in namespace "acceptance"
