package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class MyProfilePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Page Locators
    private final By profileNameHeader = By.cssSelector(".profile-info h2");
    private final By profileEmailInfo = By.cssSelector(".profile-info p");

    // Account Settings Form
    private final By fullNameInput = By.id("name");
    private final By saveChangesBtn = By.cssSelector("form.form button.submit-button");

    // Delivery Address Locators
    private final By addAddressBtn = By.cssSelector("button.add-address-btn");

    // --- Modal Locators ---
    private final By modalOverlay = By.cssSelector(".modal-overlay");
    private final By modalHeader = By.cssSelector(".modal-header h2");
    private final By modalAddressLine1Input = By.id("addressLine1");
    private final By modalCityInput = By.id("city");
    private final By modalStateInput = By.id("state");
    private final By modalPostalCodeInput = By.id("postalCode");

    // UPDATED LOCATOR: Finds the button within the modal by its class and text
    private final By modalSaveAddressBtn = By.xpath(
            "//div[contains(@class, 'modal-overlay')]" + // Ensure we are in the modal
                    "//button[contains(@class, 'submit-button') and normalize-space()='Save Address']"
    );

    private final By modalCloseBtn = By.cssSelector(".modal-header .close-btn");

    public MyProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        driver.get("http://localhost:4200/my-profile");
    }

    public String getProfileName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(profileNameHeader)).getText();
    }

    public String getProfileEmail() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(profileEmailInfo)).getText();
    }

    // --- Actions ---

    public void updateName(String newName) {
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(fullNameInput));
        nameInput.clear();
        nameInput.sendKeys(newName);
        wait.until(ExpectedConditions.elementToBeClickable(saveChangesBtn)).click();
    }

    // --- Modal Actions ---

    public void openAddAddressModal() {
        wait.until(ExpectedConditions.elementToBeClickable(addAddressBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalOverlay));
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalHeader));
    }

    public void fillNewAddressInModal(String line1, String city, String state, String postalCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalAddressLine1Input)).sendKeys(line1);
        driver.findElement(modalCityInput).sendKeys(city);
        driver.findElement(modalStateInput).sendKeys(state);
        driver.findElement(modalPostalCodeInput).sendKeys(postalCode);
    }

    public void saveAddressInModal() {
        // Wait for button to be clickable (i.e., form is valid)
        wait.until(ExpectedConditions.elementToBeClickable(modalSaveAddressBtn)).click();
        // Wait for modal to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
    }

    public void closeModal() {
        wait.until(ExpectedConditions.elementToBeClickable(modalCloseBtn)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
    }

    public boolean isModalVisible() {
        try {
            return driver.findElement(modalOverlay).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAddressVisibleInList(String addressLine1) {
        By addressLocator = By.xpath(
                String.format("//div[@class='address-details']/strong[contains(text(), '%s')]", addressLine1)
        );
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(addressLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}