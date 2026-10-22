package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BrowseMedicinesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchInput = By.cssSelector("input[type='text']");
    private final By cartBadge = By.cssSelector(".quantity-display");
    private final By cartLink = By.partialLinkText("Cart");
    // Locates the first available "Add to Cart" button for generic setup steps
    private final By anyAddToCartBtn = By.cssSelector("button.add-to-cart-button");

    // Dynamic XPaths for interacting with specific products based on their name
    private final String PRODUCT_CARD = "//h2[contains(@class, 'medicine-name') and contains(text(), '%s')]/ancestor::div[contains(@class, 'card')]";
    private final String ADD_TO_CART_BTN = PRODUCT_CARD + "//button[contains(@class, 'add-to-cart-button')]";
    // New locator for the stepper container to verify state changes
    private final String STEPPER_CONTAINER = PRODUCT_CARD + "//div[contains(@class, 'quantity-stepper')]";
    private final String INCREASE_QTY_BTN = STEPPER_CONTAINER + "//button[contains(@class, 'stepper-btn') and contains(text(), '+')]";

    public BrowseMedicinesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        driver.get("http://localhost:4200/browse-medicines");
    }

    public void searchFor(String query) {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        search.clear();
        search.sendKeys(query);
        // Optional: Wait for grid to update if necessary.
        // Ideally, wait for a specific element that signals search completion.
    }

    public boolean isProductVisible(String productName) {
        String xpath = String.format(PRODUCT_CARD, productName);
        return !driver.findElements(By.xpath(xpath)).isEmpty();
    }

    public void clickAddToCart(String productName) {
        // 1. Click the button
        By btnLocator = By.xpath(String.format(ADD_TO_CART_BTN, productName));
        wait.until(ExpectedConditions.elementToBeClickable(btnLocator)).click();

        // 2. CRITICAL SYNC: Wait for the stepper to appear.
        // This ensures the app has finished adding the item before we proceed.
        By stepperLocator = By.xpath(String.format(STEPPER_CONTAINER, productName));
        wait.until(ExpectedConditions.visibilityOfElementLocated(stepperLocator));
    }

    public void addAnyProductToCart() {
        try {

            wait.until(ExpectedConditions.elementToBeClickable(anyAddToCartBtn)).click();
            // Wait for ANY stepper to appear as confirmation of success
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".quantity-stepper")));
        } catch (Exception e) {
            throw new RuntimeException("Could not add any product to cart. Is the product list loaded?", e);
        }
    }

    public void increaseQuantity(String productName) {
        By btnLocator = By.xpath(String.format(INCREASE_QTY_BTN, productName));
        // Wait for visibility first to ensure smooth Angular transitions
        wait.until(ExpectedConditions.visibilityOfElementLocated(btnLocator));
        wait.until(ExpectedConditions.elementToBeClickable(btnLocator)).click();
        // Small pause to allow standard UI debounce if necessary
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
    }

    public String getCartBadgeCount() {
        try {
            // Use a shorter wait for the badge, as it should be immediate if present
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
        } catch (Exception e) {
            return "0";
        }
    }

    public void navigateToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
    }
}