@api @medicine
Feature: Medicine Management
  As a user, I want to view medicines.
  As an admin, I want to manage the medicine inventory.

  Background:
    Given I am a logged-in user

  Scenario: A user can get all medicines
    When I send a GET request to "/api/medicines"
    Then I should receive a 200 status code

  Scenario: A user cannot add a new medicine (403 Forbidden)
    Given I prepare a new medicine request
    When I send a POST request to "/api/medicines"
    Then I should receive a 403 status code
    And the response body should contain a forbidden error message

  @admin
  Scenario: An admin can add a new medicine
    Given I am logged in as an admin
    And I prepare a new medicine request
    When I send a POST request to "/api/medicines"
    Then I should receive a 201 status code
    And I save the medicine's ID

  @admin
  Scenario: An admin can update a medicine
    Given I am logged in as an admin
    And I have created a medicine
    And I prepare an update for that medicine
    When I send a PUT request to "/api/medicines/{MEDICINE_ID}"
    Then I should receive a 200 status code

  @admin
  Scenario: An admin can delete a medicine
    Given I am logged in as an admin
    And I have created a medicine
    When I send a DELETE request to "/api/medicines/{MEDICINE_ID}"
    Then I should receive a 204 status code