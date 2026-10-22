package com.example.api.stepdefinitions;

import com.example.api.context.APITestContext;
import io.cucumber.java.After;

public class APIHooks {

    private final APITestContext context;

    public APIHooks(APITestContext context) {
        this.context = context;
    }

    /**
     * This hook runs after every single API scenario.
     * It calls the reset() method on the context to clear all
     * stored data (tokens, responses, etc.) for the current thread.
     */
    @After("@api") // We use a tag to ensure this only runs for API tests
    public void afterApiScenario() {
        context.reset();
    }
}