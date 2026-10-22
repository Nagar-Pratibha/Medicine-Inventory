@api @cart
Feature: Shopping Cart Management
  As a logged-in user, I want to manage items in my shopping cart.

  Background:
    Given I am a logged-in user
    And I have created a medicine

  Scenario: A user adds an item to the cart
    When I add the medicine to my cart with quantity 2
    Then I should receive a 200 status code
    And the cart should contain the medicine with quantity 2

  Scenario: A user updates the quantity of an item in the cart
    Given I have added the medicine to my cart
    When I update the cart item quantity to 5
    Then I should receive a 200 status code
    And the cart should contain the medicine with quantity 5

  Scenario: A user removes an item from the cart
    Given I have added the medicine to my cart
    When I remove the item from the cart
    Then I should receive a 200 status code
    And the cart should not contain the medicine

  Scenario: A user clears the entire cart
    Given I have added the medicine to my cart
    When I send a DELETE request to "/api/users/{USER_ID}/cart"
    Then I should receive a 204 status code
    And the cart should be empty