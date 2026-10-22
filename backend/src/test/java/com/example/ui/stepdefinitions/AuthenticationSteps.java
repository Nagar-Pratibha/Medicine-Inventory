package com.example.ui.stepdefinitions;

import com.example.ui.context.TestContext;
import com.example.ui.pages.SigninPage;
import com.example.ui.pages.SignupPage;
import io.cucumber.java.en.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class AuthenticationSteps {
    private final TestContext testContext;
    private final SigninPage signinPage;
    private final SignupPage signupPage;

    public AuthenticationSteps(TestContext testContext) {
        this.testContext = testContext;
        this.signinPage = new SigninPage(testContext.getDriver());
        this.signupPage = new SignupPage(testContext.getDriver());
    }

    // --- SIGNIN STEPS ---
    @Given("I am on the Signin page")
    public void i_am_on_the_signin_page() {
        signinPage.navigateTo();
    }

    @When("I login with {string} and {string}")
    public void i_login_with_credentials(String email, String password) {
        signinPage.performLogin(email, password);
    }

    @When("I login with registered credentials {string} and {string}")
    public void i_login_with_registered_credentials(String email, String password) {
        signinPage.performLogin(email, password);
    }

    @Then("I should see an error message {string}")
    public void i_should_see_error_message(String expectedError) {
        Assert.assertEquals(signinPage.getErrorMessage(), expectedError);
    }

    @Then("I should remain on the Signin page")
    public void i_should_remain_on_the_signin_page() {
        String currentUrl = testContext.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/signin"),
                "Expected to remain on Signin page, but was on: " + currentUrl);
    }

    // --- SIGNUP STEPS ---
    @Given("I am on the Signup page")
    public void i_am_on_the_signup_page() {
        signupPage.navigateTo();
    }

    @When("I register with a valid {string}, unique email, and {string}")
    public void i_register_with_valid_data(String name, String password) {
        String uniqueEmail = "testuser_" + System.currentTimeMillis() + "@example.com";
        signupPage.performSignup(name, uniqueEmail, password);
    }

    @Then("I should be redirected to the Dashboard or Signin page")
    public void i_should_be_redirected() {
        WebDriver driver = testContext.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Handle potential Success Alert
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            // Accept any success-like alert
            if (alertText.toLowerCase().contains("success")) {
                alert.accept();
            }
        } catch (TimeoutException e) {
            // No alert, continue
        }

        // 2. Check for redirection to either Signin (if manual login required) or Browse Medicines (auto-login)
        try {
            wait.until(d -> d.getCurrentUrl().contains("/browse-medicines") || d.getCurrentUrl().contains("/signin"));
        } catch (TimeoutException e) {
            Assert.fail("Redirection failed after signup. Current URL: " + driver.getCurrentUrl());
        }
    }

    // --- NAVIGATION / COMMON STEPS ---
    @Then("I should be redirected to the Dashboard")
    public void i_should_be_redirected_to_dashboard() {
        WebDriverWait wait = new WebDriverWait(testContext.getDriver(), Duration.ofSeconds(10));
        try {
            // UPDATED: explicitly wait for '/browse-medicines'
            wait.until(d -> d.getCurrentUrl().contains("/browse-medicines"));
        } catch (Exception e) {
            Assert.fail("Failed to redirect to Dashboard (Browse Medicines). Current URL: " + testContext.getDriver().getCurrentUrl());
        }
    }

    @When("I attempt to navigate back to the Signin page")
    public void i_attempt_to_navigate_back_to_signin() {
        signinPage.navigateTo();
    }

    @Then("I should remain on the Dashboard page")
    public void i_should_remain_on_dashboard() {
        String currentUrl = testContext.getDriver().getCurrentUrl();
        // UPDATED: verify we are still on '/browse-medicines'
        Assert.assertTrue(currentUrl.contains("/browse-medicines"),
                "Auth guard failed! User accessed Signin page while logged in. Current URL: " + currentUrl);
    }
}