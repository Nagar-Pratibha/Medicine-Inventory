@api @user
Feature: User Profile Management
  As a logged-in user, I want to manage my user profile.

  Background:
    Given I am a logged-in user

  Scenario: A logged-in user retrieves their own profile
    When I send a GET request to "/api/users/{USER_ID}"
    Then I should receive a 200 status code
    And the response body should contain my user details

  Scenario: A logged-in user updates their profile
    Given I prepare an update request with a new name
    When I send a PUT request to "/api/users/{USER_ID}"
    Then I should receive a 200 status code
    And the response body should contain the updated name

  Scenario: An unauthenticated user fails to retrieve a profile
    When I send a GET request without an auth token to "/api/users/{USER_ID}"
    Then I should receive a 401 status code