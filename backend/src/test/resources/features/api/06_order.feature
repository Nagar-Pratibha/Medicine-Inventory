@api @order
Feature: Order Management
  As a user, I want to place orders for medicines.
  As an admin, I want to view and manage all orders.

  Background:
    Given I am a logged-in user
    And I have created a medicine
    And I have added an address

  Scenario: A user places a new order
    Given I have added the medicine to my cart
    When I place an order with my address
    Then I should receive a 201 status code
    And the response body should contain the order details with status "PENDING"
    And I save the order ID
    And the cart should be empty

  Scenario: A user retrieves their orders
    Given I have placed an order
    When I send a GET request to "/api/users/{USER_ID}/orders"
    Then I should receive a 200 status code
    And the response body should be a list containing the placed order

  @admin
  Scenario: An admin retrieves all orders
    Given I have placed an order
    And I am logged in as an admin
    When I send a GET request to "/api/admin/orders"
    Then I should receive a 200 status code
    And the response body should contain the user's order

  @admin
  Scenario: An admin updates order status
    Given I have placed an order
    And I am logged in as an admin
    When I update the order status to "DELIVERED"
    Then I should receive a 200 status code
    And the order status should be "DELIVERED"