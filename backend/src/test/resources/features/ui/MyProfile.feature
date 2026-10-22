
Feature: My Profile Management
  As a logged-in user, I want to manage my profile information and delivery addresses.

  Background:
    Given I am logged in as a registered user
    And I am on the My Profile page

  Scenario: Verify profile information is displayed
    # Verifies the data from the Background step
    Then I should see my registered name and email in the profile header

  Scenario: Update profile name
    When I update my full name to "Shopper Updated"
    Then I should see a success toast message "Profile updated successfully!"
    # This step now verifies the *new* name and the *original* email
    And I should see my updated name "Shopper Updated" and original email in the profile header

  Scenario: Add a new delivery address via modal
    # Merges TC-PROFILE-016, TC-PROFILE-018
    When I open the Add New Address modal
    Then the Add New Address modal should be visible
    When I add a new address in the modal:
      | Address Line 1 | 456 Automation Ave |
      | City           | Testville          |
      | State          | TS                 |
      | Postal Code    | 90210              |
    Then I should see a success toast message "Address saved successfully!"
    And the Add New Address modal should be hidden
    And I should see the new address "456 Automation Ave" in the address list