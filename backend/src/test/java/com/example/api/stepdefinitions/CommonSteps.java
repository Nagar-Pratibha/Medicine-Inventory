package com.example.api.stepdefinitions;

import com.example.api.context.APITestContext;
import com.example.api.utils.SpecBuilder;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class CommonSteps {

    private final APITestContext context;

    public CommonSteps(APITestContext context) {
        this.context = context;
    }

    /**
     * Helper to get the currently prepared request or create a new one if none exists.
     * It automatically adds the auth token if one is available in the context.
     */
    private RequestSpecification getOrCreateRequest() {
        RequestSpecification request = context.getRequest();
        if (request == null) {
            String token = context.get(APITestContext.ContextKeys.AUTH_TOKEN);
            if (token != null) {
                request = given().spec(SpecBuilder.getRequestSpec(token));
            } else {
                request = given().spec(SpecBuilder.getRequestSpec());
            }
            context.set(APITestContext.ContextKeys.REQUEST, request);
        }
        return request;
    }

    @When("I send a POST request to {string}")
    public void iSendAPOSTRequestTo(String endpoint) {
        RequestSpecification request = getOrCreateRequest();
        addDynamicPathParams(request, endpoint);
        Response response = request.when().post(endpoint);
        context.set(APITestContext.ContextKeys.RESPONSE, response);
        // Clear request after use to ensure next step starts fresh
        context.set(APITestContext.ContextKeys.REQUEST, null);
    }

    @When("I send a PUT request to {string}")
    public void iSendAPUTRequestTo(String endpoint) {
        RequestSpecification request = getOrCreateRequest();
        addDynamicPathParams(request, endpoint);
        Response response = request.when().put(endpoint);
        context.set(APITestContext.ContextKeys.RESPONSE, response);
        context.set(APITestContext.ContextKeys.REQUEST, null);
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        RequestSpecification request = getOrCreateRequest();
        addDynamicPathParams(request, endpoint);
        Response response = request.when().get(endpoint);
        context.set(APITestContext.ContextKeys.RESPONSE, response);
        context.set(APITestContext.ContextKeys.REQUEST, null);
    }

    @When("I send a DELETE request to {string}")
    public void iSendADELETERequestTo(String endpoint) {
        RequestSpecification request = getOrCreateRequest();
        addDynamicPathParams(request, endpoint);
        Response response = request.when().delete(endpoint);
        context.set(APITestContext.ContextKeys.RESPONSE, response);
        context.set(APITestContext.ContextKeys.REQUEST, null);
    }

    @Then("I should receive a {int} status code")
    public void iShouldReceiveAStatusCode(int statusCode) {
        Response response = context.getResponse();
        Assert.assertEquals(response.getStatusCode(), statusCode);
    }

    private void addDynamicPathParams(RequestSpecification request, String endpoint) {
        if (endpoint.contains("{USER_ID}") && context.get(APITestContext.ContextKeys.USER_ID) != null) {
            request.pathParam("USER_ID", (Integer) context.get(APITestContext.ContextKeys.USER_ID));
        }
        if (endpoint.contains("{MEDICINE_ID}") && context.get(APITestContext.ContextKeys.MEDICINE_ID) != null) {
            request.pathParam("MEDICINE_ID", (Long) context.get(APITestContext.ContextKeys.MEDICINE_ID));
        }
        if (endpoint.contains("{ADDRESS_ID}") && context.get(APITestContext.ContextKeys.ADDRESS_ID) != null) {
            request.pathParam("ADDRESS_ID", (Long) context.get(APITestContext.ContextKeys.ADDRESS_ID));
        }
        if (endpoint.contains("{ORDER_ID}") && context.get(APITestContext.ContextKeys.ORDER_ID) != null) {
            request.pathParam("ORDER_ID", (Long) context.get(APITestContext.ContextKeys.ORDER_ID));
        }
    }
}