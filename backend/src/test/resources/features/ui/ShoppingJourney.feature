Feature: Medicine Shopping Journey
  As a logged-in user, I want to search for medicines, add them to my cart, and place an order.

  Background:
    Given I am logged in as a registered user

  # Merges Search TCs (TC-BROWSE-010 to 013) and Add to Cart (TC-BROWSE-014 to 018)
  @ignore
  Scenario: Search for medicines and manage cart
    When I search for "Paracetamol" on the Browse page
    Then I should see "Paracetamol 500mg Tab" in the results
    When I add "Paracetamol 500mg Tab" to the cart
    And I increase the quantity to 2
    Then the cart badge should display "2"
    And the item subtotal in the cart should be "₹11.00"

  # Merges full E2E flow from Cart (TC-CART-012) through Delivery (TC-DELIVERY-027)
  Scenario: Successful End-to-End checkout
    Given I have items in my cart
    When I proceed to checkout from the Cart page
    And I save a new delivery address:
      | Address Line 1 | 123 Health St |
      | City           | Wellness City |
      | State          | WB            |
      | Postal Code    | 700001        |
    And I click "Place Order"
    Then I should see an "Order placed successfully!" confirmation
    And I should see the new order in my Order History