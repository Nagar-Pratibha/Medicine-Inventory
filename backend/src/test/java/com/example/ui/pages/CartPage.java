package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By proceedToCheckoutBtn = By.cssSelector("button.checkout-button");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Dynamic locator to find the subtotal cell for a specific product row
    public String getSubtotalForProduct(String productName) {
        // Matched to your HTML structure:
        // <td class="td-product" data-label="Product">Name</td> ... <td class="td-subtotal" data-label="Subtotal">Value</td>
        String xpath = String.format(
                "//td[contains(@class, 'td-product') and normalize-space(text())='%s']/parent::tr//td[contains(@class, 'td-subtotal')]",
                productName
        );
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))).getText().trim();
    }

    public void clickProceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutBtn)).click();
    }
}