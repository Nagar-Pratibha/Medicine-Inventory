package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SignupPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // standard By locators based on your provided HTML snippets
    private final By nameInputLocator = By.id("name");
    private final By emailInputLocator = By.id("email");
    private final By passwordInputLocator = By.id("password");
    // Adjust css selector if your button has a different class, e.g., button[type='submit']
    private final By createAccountButtonLocator = By.cssSelector("button.submit-button");

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        // 10-second explicit wait
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        // Update port if different
        driver.get("http://localhost:4200/signup");
    }

    public void enterName(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInputLocator)).sendKeys(name);
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInputLocator)).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInputLocator)).sendKeys(password);
    }

    public void clickCreateAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(createAccountButtonLocator)).click();
    }

    // Helper method for the complete flow
    public void performSignup(String name, String email, String password) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        clickCreateAccount();
    }
}