package com.example.api.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        // Path to your API feature files
        features = "src/test/resources/features/api",

        // Package where your step definitions and hooks are
        glue = "com.example.api.stepdefinitions",

        // Tags to run (e.g., run only @auth tests)
        // tags = "@auth",

        // Plugin for readable console logs
        plugin = {"pretty"}
)

public class APIRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel execution of scenarios.
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}