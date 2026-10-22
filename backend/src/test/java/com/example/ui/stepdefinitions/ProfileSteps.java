package com.example.ui.stepdefinitions;

import com.example.ui.context.TestContext;
import com.example.ui.pages.MyProfilePage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.Map;

public class ProfileSteps {
    private final TestContext testContext;
    private final MyProfilePage profilePage;

    public ProfileSteps(TestContext testContext) {
        this.testContext = testContext;
        this.profilePage = new MyProfilePage(testContext.getDriver());
    }

    @Given("I am on the My Profile page")
    public void i_am_on_the_my_profile_page() {
        profilePage.navigateTo();
    }

    // UPDATED: This step now reads from the TestContext for dynamic data
    @Then("I should see my registered name and email in the profile header")
    public void i_should_see_my_registered_name_and_email() {
        // Retrieve expected data saved in the context during login/signup
        String expectedName = testContext.getName();
        String expectedEmail = testContext.getEmail();

        Assert.assertEquals(profilePage.getProfileName(), expectedName, "Profile name does not match context.");
        Assert.assertEquals(profilePage.getProfileEmail(), expectedEmail, "Profile email does not match context.");
    }

    @When("I update my full name to {string}")
    public void i_update_my_full_name_to(String newName) {
        profilePage.updateName(newName);
        // Store the new name in the context in case other steps need it
        testContext.setName(newName);
    }

    // NEW: Step to validate the updated name and original email
    @Then("I should see my updated name {string} and original email in the profile header")
    public void i_should_see_my_updated_name_and_original_email(String newName) {
        // Retrieve original email from context
        String expectedEmail = testContext.getEmail();

        Assert.assertEquals(profilePage.getProfileName(), newName, "Profile name was not updated correctly.");
        Assert.assertEquals(profilePage.getProfileEmail(), expectedEmail, "Profile email should not have changed.");
    }

    @Then("I should see a success toast message {string}")
    public void i_should_see_a_success_toast(String expectedMessage) {
        // This robust XPath finds the success container, then looks
        // inside it for the toast-message element that *contains* the expected text.
        By toastLocator = By.xpath(
                String.format(
                        "//div[contains(@class, 'toast-container') and contains(@class, 'success')]" +
                                "//div[contains(@class, 'toast-message') and contains(text(), '%s')]",
                        expectedMessage
                )
        );

        WebDriverWait wait = new WebDriverWait(testContext.getDriver(), Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(toastLocator));
        } catch (TimeoutException e) {
            // This will now only fail if the toast really doesn't appear.
            Assert.fail("Did not see success toast containing text: '" + expectedMessage + "'");
        }
    }

    @When("I open the Add New Address modal")
    public void i_open_the_add_new_address_modal() {
        profilePage.openAddAddressModal();
    }

    @Then("the Add New Address modal should be visible")
    public void the_add_new_address_modal_should_be_visible() {
        Assert.assertTrue(profilePage.isModalVisible(), "Add Address modal is not visible.");
    }

    @When("I close the modal")
    public void i_close_the_modal() {
        profilePage.closeModal();
    }

    @Then("the Add New Address modal should be hidden")
    public void the_add_new_address_modal_should_be_hidden() {
        Assert.assertFalse(profilePage.isModalVisible(), "Add Address modal is still visible.");
    }

    @When("I add a new address in the modal:")
    public void i_add_a_new_address_in_the_modal(DataTable dataTable) {
        Map<String, String> address = dataTable.asMap(String.class, String.class);
        profilePage.fillNewAddressInModal(
                address.get("Address Line 1"),
                address.get("City"),
                address.get("State"),
                address.get("Postal Code")
        );
        profilePage.saveAddressInModal();
    }

    // UPDATED: Uses the helper method from MyProfilePage for a cleaner assertion
    @Then("I should see the new address {string} in the address list")
    public void i_should_see_the_new_address_in_the_list(String addressLine1) {
        // This assertion uses the robust helper method from your Page Object
        Assert.assertTrue(profilePage.isAddressVisibleInList(addressLine1),
                "New address did not appear in the list: '" + addressLine1 + "'");
    }
}