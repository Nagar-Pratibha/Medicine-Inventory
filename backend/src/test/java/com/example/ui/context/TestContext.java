package com.example.ui.context;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;

public class TestContext {
    private WebDriver driver;
    private String name;
    private String email;

    public WebDriver getDriver() {
        if (driver == null) {
            // Calls the static method from your new WebDriverManager
            driver = WebDriverManager.getDriver();
        }
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}