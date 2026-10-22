package com.example.api.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class SpecBuilder {

    // Set your Spring Boot application's base URL and port
    // This could also be loaded from a properties file
    private static final String BASE_URL = "http://localhost:8099";

    /**
     * Creates a base RequestSpecification.
     * - Sets the base URL
     * - Sets the content type to JSON
     * - Logs the request details if validation fails
     */
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL) // Log everything about the request
                .build();
    }

    /**
     * Creates a RequestSpecification that includes an auth token.
     * @param token The JWT token
     */
    public static RequestSpecification getRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer " + token)
                .log(LogDetail.ALL)
                .build();
    }

    /**
     * Creates a base ResponseSpecification.
     * - Logs the response details if validation fails
     */
    public static ResponseSpecification getResponseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL) // Log everything about the response
                .build();
    }
}