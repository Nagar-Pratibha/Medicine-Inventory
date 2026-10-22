package com.example.ui.context;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class WebDriverManager {

    public static WebDriver getDriver() {
        // Selenium 4 automatically manages the driver binary.
        // No need for System.setProperty("webdriver.chrome.driver", path)

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
//        options.addArguments("--headless=new"); // Uncomment for headless mode

        WebDriver driver = new ChromeDriver(options);

        // Set default timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        return driver;
    }
}