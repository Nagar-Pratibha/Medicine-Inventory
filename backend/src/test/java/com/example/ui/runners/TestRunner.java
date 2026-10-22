package com.example.ui.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features/ui",
        glue = "com.example.ui.stepdefinitions",

        plugin = {
                "pretty",
                "html:target/cucumber-reports/ui-pretty.html",
                "json:target/cucumber-reports/uiTestReport.json"
        },
        monochrome = true,
        tags = "not @ignore"
)

public class TestRunner extends AbstractTestNGCucumberTests {
    // Enable parallel execution if needed
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}