package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHistoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators based on the test plan [cite: 551] and HTML
    private final By loadingIndicator = By.className("loading-indicator");
    private final By ordersTable = By.className("orders-table");
    private final By orderRows = By.cssSelector("tbody tr");
    private final By noOrdersMessage = By.className("no-orders"); // [cite: 552]

    // Dynamic locators
    private String orderRowBaseXPath = "//td[normalize-space()='%s']/parent::tr";

    public OrderHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        driver.get("http://localhost:4200/orders");
    }

    /**
     * Waits for the page to finish loading and returns the number of order rows displayed.
     */
    public int getOrderCount() {
        // Wait for the loading spinner to disappear [cite: 570]
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingIndicator));

        try {
            // Wait for the table to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(ordersTable));
            // Return the count of order rows
            return driver.findElements(orderRows).size();
        } catch (Exception e) {
            // If table doesn't appear, check if "no orders" message is present [cite: 503]
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(noOrdersMessage));
                return 0; // No orders found
            } catch (Exception e2) {
                throw new RuntimeException("Order History page did not load table or 'No Orders' message.", e2);
            }
        }
    }

    /**
     * Gets the status text for a specific order ID. [cite: 560]
     * @param orderId The text of the order ID (e.g., "#1031")
     * @return The status text (e.g., "Pending")
     */
    public String getStatusForOrder(String orderId) {
        String statusXPath = String.format(orderRowBaseXPath, orderId) + "//span[contains(@class, 'status')]";
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(statusXPath))).getText();
    }

    /**
     * Gets the list of items for a specific order ID. [cite: 562]
     * @param orderId The text of the order ID (e.g., "#1031")
     * @return A List of strings, e.g., ["Paracetamol 500mg Tab (x2)"]
     */
    public List<String> getItemsForOrder(String orderId) {
        String itemsXPath = String.format(orderRowBaseXPath, orderId) + "//ul[contains(@class, 'item-list')]/li";
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(itemsXPath)))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}