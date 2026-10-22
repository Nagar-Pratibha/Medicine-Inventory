package com.example.api.context;


import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages shared state for a single Cucumber scenario using ThreadLocal.
 * This ensures that state is isolated between parallel test executions.
 */
public class APITestContext {

    // Use ThreadLocal to ensure thread-safety for parallel execution
    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

    // Keys for storing and retrieving common objects
    public enum ContextKeys {
        USER_ID,
        USER_EMAIL,
        USER_PASSWORD,
        AUTH_TOKEN,
        REQUEST,
        RESPONSE,
        ORDER_ID,
        ADDRESS_ID,
        MEDICINE_ID,
        CART_ITEM_ID
    }

    /**
     * Saves an object into the context map.
     * @param key The enum key (ContextKeys)
     * @param value The object to save (e.g., token, response)
     */
    public void set(ContextKeys key, Object value) {
        context.get().put(key.name(), value);
    }

    /**
     * Retrieves an object from the context map.
     * @param key The enum key (ContextKeys)
     * @return The stored object
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ContextKeys key) {
        return (T) context.get().get(key.name());
    }

    /**
     * Helper to get the saved RequestSpecification.
     */
    public RequestSpecification getRequest() {
        return get(ContextKeys.REQUEST);
    }

    /**
     * Helper to get the saved Response.
     */
    public Response getResponse() {
        return get(ContextKeys.RESPONSE);
    }

    /**
     * Clears the context for the current thread.
     * Should be called after each scenario (e.g., in an @After hook).
     */
    public void reset() {
        context.get().clear();
        context.remove();
    }
}