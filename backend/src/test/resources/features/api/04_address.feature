@api @address
Feature: Address Management
  As a logged-in user, I want to manage my shipping addresses.

  Background:
    Given I am a logged-in user

  Scenario: A user adds a new address
    Given I prepare a new address request
    When I send a POST request to "/api/users/{USER_ID}/addresses"
    Then I should receive a 201 status code
    And the response body should contain the new address details
    And I save the address ID

  Scenario: A user retrieves all their addresses
    Given I have added an address
    When I send a GET request to "/api/users/{USER_ID}/addresses"
    Then I should receive a 200 status code
    And the response body should be a list containing at least one address

  Scenario: A user sets an address as default
    Given I have added an address
    When I send a PUT request to "/api/users/{USER_ID}/addresses/{ADDRESS_ID}/default"
    Then I should receive a 200 status code
    And the response body should show the address is default

  Scenario: A user deletes an address
    Given I have added an address
    When I send a DELETE request to "/api/users/{USER_ID}/addresses/{ADDRESS_ID}"
    Then I should receive a 204 status code