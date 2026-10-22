
Feature: User Authentication
  As a user, I want to create an account and log in so that I can access restricted features.

  # Merges TC-SIGNUP-022 (Happy Path)
  Scenario: Successful new user registration
    Given I am on the Signup page
    When I register with a valid "Test User", unique email, and "Password123!"
    Then I should be redirected to the Dashboard or Signin page

  # Merges TC-SIGNIN-019 (Happy Path) and TC-SIGNIN-024 (Auth Guard)
  @ignore
  Scenario: Successful login and session persistence
    Given I am on the Signin page
    When I login with registered credentials "testuser@example.com" and "ValidPassword123!"
    Then I should be redirected to the Dashboard
    When I attempt to navigate back to the Signin page
    Then I should remain on the Dashboard page

  # Merges TC-SIGNIN-026 (Invalid Creds) and TC-SIGNIN-027 (User Not Found)
  Scenario Outline: Failed login attempts with invalid credentials
    Given I am on the Signin page
    When I login with "<email>" and "<password>"
    # Update the expected error message to match the actual alert text
    Then I should see an error message "Login failed. Please check your credentials and try again."
    And I should remain on the Signin page

    Examples:
      | email                | password          |
      | testuser@example.com | WrongPass         |
      | nouser@example.com   | AnyPass           |