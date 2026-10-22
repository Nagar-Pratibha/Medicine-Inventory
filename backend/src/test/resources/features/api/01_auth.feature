@auth
Feature: Authentication
  As a new user, I want to register and log in to the application.

  Scenario: A new user registers successfully
    Given I prepare a new user registration request
    When I send a POST request to "/api/users/register"
    Then I should receive a 201 status code
    And the response body should contain the user's details
    And I save the user's ID

  Scenario: An existing user logs in successfully
    Given I am a registered user
    When I prepare a login request with my credentials
    And I send a POST request to "/api/auth/login"
    Then I should receive a 200 status code
    And the response body should contain an auth token
    And I save the auth token and user ID for subsequent requests

  Scenario: A user fails to log in with an incorrect password
    Given I am a registered user
    When I prepare a login request with an incorrect password
    And I send a POST request to "/api/auth/login"
    Then I should receive a 401 status code