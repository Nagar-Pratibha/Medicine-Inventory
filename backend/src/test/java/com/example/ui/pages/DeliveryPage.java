package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DeliveryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Updated Locators based on
    private final By addNewAddressToggleBtn = By.xpath("//button[contains(text(), 'Add new address')]");
    private final By postalCodeInput = By.id("postalCode"); // New required field
    private final By addressLine1Input = By.id("addressLine1");
    private final By cityInput = By.id("city");
    private final By stateInput = By.id("state");
    // Matched exactly to HTML button text
    private final By saveAddressBtn = By.xpath("//button[normalize-space(text())='Save and Use Address']");
    // Ensure Place Order button locator is correct (it might be outside the form, check full HTML if this fails)
    private final By placeOrderBtn = By.xpath("//button[contains(text(), 'Place Order')]");

    public DeliveryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickAddNewAddress() {
        wait.until(ExpectedConditions.elementToBeClickable(addNewAddressToggleBtn)).click();
    }

    public void fillAddress(String line1, String city, String state, String postalCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(addressLine1Input)).sendKeys(line1);
        driver.findElement(cityInput).sendKeys(city);
        driver.findElement(stateInput).sendKeys(state);
        driver.findElement(postalCodeInput).sendKeys(postalCode);
    }

    public void clickSaveAddress() {
        // Wait for button to be clickable (Angular form validation might delay it slightly)
        wait.until(ExpectedConditions.elementToBeClickable(saveAddressBtn)).click();
    }

    public void clickPlaceOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn)).click();
    }
}